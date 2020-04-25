var serverContext = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

$(document).ready(function () {
    $('form').submit(function (event) {
        resetPass(event);
    });
});

function resetPass(event) {
    event.preventDefault();
    var email = $("#email").val();
    if (email == '') {
        alert("Email must not be empty");
        return;
    }
    $.post(serverContext + "/user/resetPassword", {email: email})
        .done(function (data) {
            if (data != null) {
                window.location.href =
                    serverContext + "/login?message=Reset password mail sent";
            }
            else {
                alert("Fail")
            }

        })
        .fail(function (data) {
            window.location.href =
                serverContext + "/login?message=Error while sending mail (user not found)";

        });
}

$(document).ajaxStart(function () {
    $("title").html("LOADING ...");
});