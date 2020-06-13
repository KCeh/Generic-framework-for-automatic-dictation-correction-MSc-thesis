$(document).ready(function() {
    $("#info-div")[0].hidden=true;
    $("#error-div")[0].hidden=true;
    $("#image-upload").on("change", uploadFiles);
    $("#submit-btn").on("click", displayWait)
    //if($("#originalImageUrl-input")[0].value.length>0){//correct!
    //   $("#info-div")[0].innerHTML="Image saved! No need to upload it again";
    //    $("#info-div")[0].hidden=false;
    //}

});

function uploadFiles() {
    $("#submit-btn")[0].disabled=true;
    $("#back-btn")[0].disabled=true;
    $("#error-div")[0].hidden=true;
    $("#info-div")[0].hidden=true;
    var data = new FormData();
    var list= $('#image-upload')[0].files;
    var fileList=[];
    for(var i=0, len=list.length;i<len;i++){
        //fileList.push(list[i]);
        data.append('file',  list[i]);
    }

    //data.append('file',  fileList);

    //pocisti stare fajlove
    $("#info-div")[0].innerHTML="Uploading...";
    $("#info-div")[0].hidden=false;

    $.ajax({
        url: "/corrections/uploadMultiple",
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
                $("#info-div")[0].innerHTML="Image(s) uploaded successfully";
                $("#info-div")[0].hidden=false;

                var respones=data["message"].substr(1);
                respones=respones.slice(0,-1);
                var list=respones.split(",");
                //console.log(list);
                var toAppend=document.getElementById("show-images");
                for(var i=0, len=list.length;i<len;i++){
                    var newDiv=document.createElement('div');

                    var element = document.createElement('img');
                    element.src=list[i].trim();
                    var name="id"+i;
                    element.setAttribute("id", name);

                    element.setAttribute("class", "img-thumbnail");
                    element.setAttribute("hight", "200");
                    element.setAttribute("width", "200");

                    var modalDiv=document.createElement('div');
                    modalDiv.setAttribute("class", "modal");
                    name="modal"+i;
                    element.setAttribute("id", name);

                    var closeSpan=document.createElement('span');
                    closeSpan.setAttribute("class", "close");
                    closeSpan.innerHTML="x";

                    var downloadSpan=document.createElement('span');
                    downloadSpan.setAttribute("value", list[i].trim());
                    downloadSpan.setAttribute("class","download");
                    var downloadIcon=document.createElement('i');
                    downloadIcon.setAttribute("class","fa fa-download");
                    downloadSpan.appendChild(downloadIcon);

                    var bigImg=document.createElement('img');
                    bigImg.setAttribute("class","modal-content");
                    name="img"+i;
                    bigImg.setAttribute("id",name);

                    //add functions
                    element.onclick=function(){
                        modalDiv.style.display = "block";
                        bigImg.src = this.src;
                        downloadSpan.setAttribute("value",this.src);
                    };
                    closeSpan.onclick=function(){
                        modalDiv.style.display = "none";
                    };
                    downloadSpan.onclick=function(){
                        window.open(downloadSpan.getAttribute("value"));
                    };

                    modalDiv.appendChild(closeSpan);
                    modalDiv.appendChild(downloadSpan);
                    modalDiv.appendChild(bigImg);
                    newDiv.appendChild(element);
                    newDiv.appendChild(modalDiv);
                    toAppend.appendChild(newDiv);
                    toAppend.appendChild( document.createTextNode( '\u00A0\u00A0' ) );
                    var br = document.createElement('br');
                    toAppend.appendChild(br);
                }
                //$("#originalImageUrl-input").val(data["message"]);
                //<img id="originalImg" th:src="*{urlOriginalImage}" alt="original" class="img-thumbnail" height="200" width="200">


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


function displayWait() {
    $("#wait-div")[0].innerHTML ="This may take a while";
    $("#wait-div")[0].hidden=false;
    $("#submit-btn")[0].disabled=true;
    $("#back-btn")[0].disabled=true;
    $("#form").submit();
}