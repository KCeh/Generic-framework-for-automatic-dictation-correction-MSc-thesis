function checkAuthority(method, user, owner, id){
    if(owner.valueOf()!=user.valueOf()){
        window.location.href = "/dictate/all?error=You+don't+have+authority+to+"+method+"+dictate.+Your+are+not+creator";
        return;
    }
    if(method.valueOf()==='edit'){
        var params="?id="+id;
        window.location.href = "/dictate/edit"+params;
    }else if(method.valueOf()==='delete'){
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/dictate/delete", true);
        var params="id="+id;
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhr.send(params);

        xhr.onload = function() {
            var data = JSON.parse(this.responseText);
            if(data['message'].valueOf()==="Error"){
                url = "/dictate/my?error="+data["error"]
                window.location.href = url;
            }else {
                window.location.href = "/dictate/my?message="+data["message"];
            }
        }

        xhr.onerror = function () {
            window.location.href = "/dictate/my?error=Something+went+wrong";
        };
    }
}

function viewDetails(id) {

}