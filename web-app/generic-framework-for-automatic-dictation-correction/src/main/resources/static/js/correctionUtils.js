$(document).ready(function() {
    $("#error-div")[0].hidden=true;
    $("#info-div")[0].hidden=true;
    $("#image-upload").on("change", uploadFile);
    $("#submit-btn").on("click", displayWait)
});

function uploadFile() {
    $("#submit-btn")[0].disabled=true;
    $("#back-btn")[0].disabled=true;
    $("#error-div")[0].hidden=true;
    $("#info-div")[0].hidden=true;
    var data = new FormData();
    data.append('file',  $('#image-upload')[0].files[0]);

    $("#info-div")[0].innerHTML="Uploading...";
    $("#info-div")[0].hidden=false;

    $.ajax({
        url: "/corrections/uploadImage",
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
                $("#submit-btn")[0].disabled=false;
                $("#back-btn")[0].disabled=false;
            }else {
                $("#info-div")[0].innerHTML="Image uploaded successfully";
                $("#info-div")[0].hidden=false;
                $("#originalImageUrl-input").val(data["message"]);
                $("#submit-btn")[0].disabled=false;
                $("#back-btn")[0].disabled=false;
            }
        },
        error: function () {
            $("#error-div")[0].innerHTML ="Something went wrong!";
            $("#error-div")[0].hidden=false;
            $("#submit-btn")[0].disabled=false;
            $("#back-btn")[0].disabled=false;
        }
    });
}

function deleteCorrection(id) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/corrections/delete", true);
    var params="id="+id;
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhr.send(params);

    xhr.onload = function() {
        var data = JSON.parse(this.responseText);
        if(data['message'].valueOf()==="Error"){
            url = "/corrections/my?error="+data["error"]
            window.location.href = url;
        }else {
            window.location.href = "/corrections/my?message="+data["message"];
        }
    }

    xhr.onerror = function () {
        window.location.href = "/corrections/my?error=Something+went+wrong";
    };
}

function viewDetails(id) {
    var params="?id="+id;
    window.location.href = "/corrections/view"+params;
}

function displayWait() {
    $("#wait-div")[0].innerHTML ="This may take a while";
    $("#wait-div")[0].hidden=false;
    $("#submit-btn")[0].disabled=true;
    $("#back-btn")[0].disabled=true;
    $("#form").submit();
}