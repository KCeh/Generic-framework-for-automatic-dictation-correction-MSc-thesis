$(document).ready(function() {
    $("#error-div")[0].hidden=true;
    $("#info-div")[0].hidden=true;
    $("#info-text-div")[0].hidden=true;
    $("#audio-upload").on("change", uploadFile);
});

function uploadFile() {
    $("#error-div")[0].hidden=true;
    $("#info-div")[0].hidden=true;
    var data = new FormData();
    data.append('file',  $('#audio-upload')[0].files[0]);

    $("#info-div")[0].innerHTML="Uploading...";
    $("#info-div")[0].hidden=false;

    $.ajax({
        url: "/dictate/uploadAudio",
        type: "POST",
        data: data,
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        cache: false,
        success: function (data) {
            if(data['message'].valueOf()==="Error"){
                $("#error-div")[0].innerHTML= data["error"];
                $("#error-div")[0].hidden=false;
            }else {
                $("#info-div")[0].innerHTML="Audio uploaded successfully";
                $("#info-div")[0].hidden=false;
                $("#audioUrl-input").val(data["message"]);
            }
        },
        error: function () {
            $("#error-div")[0].innerHTML ="Something went wrong!";
            $("#error-div")[0].hidden=false;
        }
    });
}

var startRecordingButton = document.getElementById("recorder");
var stopRecordingButton = document.getElementById("stop");

var leftchannel = [];
var rightchannel = [];
var recorder = null;
var recordingLength = 0;
var volume = null;
var mediaStream = null;
var sampleRate = 44100;
var context = null;
var blob = null;

startRecordingButton.addEventListener("click", function () {
    // Initialize recorder
    navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
    navigator.getUserMedia(
        {
            audio: true
        },
        function (e) {
            // creates the audio context
            window.AudioContext = window.AudioContext || window.webkitAudioContext;
            context = new AudioContext();

            // creates an audio node from the microphone incoming stream
            mediaStream = context.createMediaStreamSource(e);

            // https://developer.mozilla.org/en-US/docs/Web/API/AudioContext/createScriptProcessor
            // bufferSize: the onaudioprocess event is called when the buffer is full
            var bufferSize = 2048;
            var numberOfInputChannels = 2;
            var numberOfOutputChannels = 2;
            if (context.createScriptProcessor) {
                recorder = context.createScriptProcessor(bufferSize, numberOfInputChannels, numberOfOutputChannels);
            } else {
                recorder = context.createJavaScriptNode(bufferSize, numberOfInputChannels, numberOfOutputChannels);
            }

            recorder.onaudioprocess = function (e) {
                leftchannel.push(new Float32Array(e.inputBuffer.getChannelData(0)));
                rightchannel.push(new Float32Array(e.inputBuffer.getChannelData(1)));
                recordingLength += bufferSize;
            }

            // we connect the recorder
            mediaStream.connect(recorder);
            recorder.connect(context.destination);
        },
        function (e) {
            console.error(e);
        });
});

stopRecordingButton.addEventListener("click", function () {

    // stop recording
    recorder.disconnect(context.destination);
    mediaStream.disconnect(recorder);

    // we flat the left and right channels down
    // Float32Array[] => Float32Array
    var leftBuffer = flattenArray(leftchannel, recordingLength);
    var rightBuffer = flattenArray(rightchannel, recordingLength);
    // we interleave both channels together
    // [left[0],right[0],left[1],right[1],...]
    var interleaved = interleave(leftBuffer, rightBuffer);

    // we create our wav file
    var buffer = new ArrayBuffer(44 + interleaved.length * 2);
    var view = new DataView(buffer);

    // RIFF chunk descriptor
    writeUTFBytes(view, 0, 'RIFF');
    view.setUint32(4, 44 + interleaved.length * 2, true);
    writeUTFBytes(view, 8, 'WAVE');
    // FMT sub-chunk
    writeUTFBytes(view, 12, 'fmt ');
    view.setUint32(16, 16, true); // chunkSize
    view.setUint16(20, 1, true); // wFormatTag
    view.setUint16(22, 2, true); // wChannels: stereo (2 channels)
    view.setUint32(24, sampleRate, true); // dwSamplesPerSec
    view.setUint32(28, sampleRate * 4, true); // dwAvgBytesPerSec
    view.setUint16(32, 4, true); // wBlockAlign
    view.setUint16(34, 16, true); // wBitsPerSample
    // data sub-chunk
    writeUTFBytes(view, 36, 'data');
    view.setUint32(40, interleaved.length * 2, true);

    // write the PCM samples
    var index = 44;
    var volume = 1;
    for (var i = 0; i < interleaved.length; i++) {
        view.setInt16(index, interleaved[i] * (0x7FFF * volume), true);
        index += 2;
    }

    // our final blob
    blob = new Blob([view], { type: 'audio/wav' });

    saveRecording(blob);

    leftchannel = [];
    rightchannel = [];
    recorder = null;
    recordingLength = 0;
    volume = null;
    mediaStream = null;
    sampleRate = 44100;
    context = null;
    blob = null;
});

function flattenArray(channelBuffer, recordingLength) {
    var result = new Float32Array(recordingLength);
    var offset = 0;
    for (var i = 0; i < channelBuffer.length; i++) {
        var buffer = channelBuffer[i];
        result.set(buffer, offset);
        offset += buffer.length;
    }
    return result;
}

function interleave(leftChannel, rightChannel) {
    var length = leftChannel.length + rightChannel.length;
    var result = new Float32Array(length);

    var inputIndex = 0;

    for (var index = 0; index < length;) {
        result[index++] = leftChannel[inputIndex];
        result[index++] = rightChannel[inputIndex];
        inputIndex++;
    }
    return result;
}

function writeUTFBytes(view, offset, string) {
    for (var i = 0; i < string.length; i++) {
        view.setUint8(offset + i, string.charCodeAt(i));
    }
}

function blobToFile(theBlob, fileName){
    theBlob.lastModifiedDate = new Date();
    theBlob.name = fileName;
    return theBlob;
}

function saveRecording(blob) {
    file=blobToFile(blob, 'recording.wav')
    var data = new FormData();
    data.append('file',  file);

    $("#info-div")[0].innerHTML="Uploading...";
    $("#info-div")[0].hidden=false;

    $.ajax({
        url: "/dictate/uploadAudio",
        type: "POST",
        data: data,
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        success: function (data) {
            if(data['message'].valueOf()==="Error"){
                $("#error-div")[0].innerHTML= data["error"];
                $("#error-div")[0].hidden=false;
            }else {
                $("#info-div")[0].innerHTML="Audio uploaded successfully";
                $("#info-div")[0].hidden=false;
                $("#audioUrl-input").val(data["message"]);
            }
        },
        error: function () {
            $("#error-div")[0].innerHTML ="Something went wrong!";
            $("#error-div")[0].hidden=false;
        }
    });
}

function transcribe() {
    $("#error-text-div")[0].hidden=true;

    var url=$("#audioUrl-input").val();
    var lang=$("#language").val();

    if(url==="" ){
        alert("No audio recorded or uploaded");
        return;
    }

    if(lang===""){
        alert("You must select language!");
        return;
    }


    $("#info-text-div")[0].innerHTML="Processing...";
    $("#info-text-div")[0].hidden=false;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/dictate/transcribe", true);
    var params="url="+url+"&code="+lang;
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhr.send(params);

    xhr.onload = function() {
        var data = JSON.parse(this.responseText);
        if(data['message'].valueOf()==="Error"){
            $("#info-text-div")[0].hidden=true;
            $("#error-text-div")[0].innerHTML =data["error"];
            $("#error-text-div")[0].hidden=false;
        }else {
            $("#text").val(data["message"]);
            $("#info-text-div")[0].hidden=true;
        }
    };

    xhr.onerror = function () {
        $("#info-text-div")[0].hidden=true;
        $("#error-text-div")[0].innerHTML ="Something went wrong!";
        $("#error-text-div")[0].hidden=false;
    };
    
}
