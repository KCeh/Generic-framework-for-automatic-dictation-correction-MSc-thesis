function promoteUser(email){
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/manageUsers/promote", true);
    var params="email="+email;
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhr.send(params);

    xhr.onload = function() {
        console.log(this.responseText);
        var data = JSON.parse(this.responseText);
        if(data['message'].valueOf()=="Error"){
            url = "/manageUsers?message="+data["error"]
            window.location.href = url;
        }else {
            window.location.href = "/manageUsers?message=User+promoted+successfully";
        }
    }

    xhr.onerror = function () {
        window.location.href = "/manageUsers?message=Error+while+promoting+user";
    };
}

function demoteUser(email){
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/manageUsers/demote", true);
    var params="email="+email;
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhr.send(params);

    xhr.onload = function() {
        console.log(this.responseText);
        var data = JSON.parse(this.responseText);
        if(data['message'].valueOf()=="Error"){
            url = "/manageUsers?message="+data["error"]
            window.location.href = url;
        }else {
            window.location.href = "/manageUsers?message=User+demoted+successfully";
        }
    }

    xhr.onerror = function () {
        window.location.href = "/manageUsers?message=Error+while+demoting+user";
    };
}