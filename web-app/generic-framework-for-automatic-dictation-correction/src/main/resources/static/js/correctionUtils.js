$(document).ready(function() {
    $("#error-div")[0].hidden=true;
    $("#info-div")[0].hidden=true;
    $("#image-upload").on("change", uploadFile);
});

function uploadFile() {
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
            }else {
                $("#info-div")[0].innerHTML="Image uploaded successfully";
                $("#info-div")[0].hidden=false;
                $("#originalImageUrl-input").val(data["message"]);
            }
        },
        error: function () {
            $("#error-div")[0].innerHTML ="Something went wrong!";
            $("#error-div")[0].hidden=false;
        }
    });
}