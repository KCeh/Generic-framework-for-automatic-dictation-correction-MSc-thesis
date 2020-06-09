$(document).ready(function () {
    $(":password").keyup(function () {
        if ($("#password").val().length<8){
            $("#lengthError").show().html("8 characters minimum");
            $("#submit")[0].disabled=true;
        } else {
            $("#lengthError").html("").hide();
            $("#submit")[0].disabled=false;
        }
    });
});