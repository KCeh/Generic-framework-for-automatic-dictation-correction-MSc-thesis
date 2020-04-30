$(document).ready(function() {
    $("#error-div")[0].hidden=true;
    $("#info-div")[0].hidden=true;
    $("#audio-upload").on("change", uploadFile);
});

function uploadFile() {
    var data = new FormData();
    data.append('file',  $('#audio-upload')[0].files[0]);

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