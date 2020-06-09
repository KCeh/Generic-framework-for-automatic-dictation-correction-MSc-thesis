//var serverContext = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 1));
$(document).ready(function () {
    $('form').submit(function (event) {
        savePass(event);
    });

    $(":password").keyup(function () {
        if ($("#password").val().length<8){
            $("#globalError").show().html("8 characters minimum");
        }else if ($("#password").val() != $("#matchPassword").val()) {
            $("#globalError").show().html("Passwords do not match!");
        } else {
            $("#globalError").html("").hide();
        }
    });
});

function savePass(event) {
    event.preventDefault();
    if ($("#password").val() != $("#matchPassword").val()) {
        $("#globalError").show().html("Passwords do not match!");
        return;
    }
    if($("#password").val().length<8){
        $("#globalError").show().html("8 characters minimum");
        return;
    }
    var formData = $('form').serialize();
    $.post("/user/savePassword", formData, function (data) {
        window.location.href = "/login?message=Password reset successfully";
    })
        .fail(function (data) {
            if (data.responseJSON.error.indexOf("InternalError") > -1) {
                window.location.href = "/login?message=Password reset fail";
            }
            else {
                var errors = $.parseJSON(data.responseJSON.message);
                $.each(errors, function (index, item) {
                    $("#globalError").show().html(item.defaultMessage);
                });
                errors = $.parseJSON(data.responseJSON.error);
                $.each(errors, function (index, item) {
                    $("#globalError").show().append(item.defaultMessage + "<br/>");
                });
            }
        });
}