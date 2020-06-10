$(document).ready(function() {
    $("#submit-btn").on("click", displayWait)
});


function displayWait() {
    $("#wait-div")[0].innerHTML ="This may take a while";
    $("#wait-div")[0].hidden=false;
    $("#submit-btn")[0].disabled=true;
    $("#back-btn")[0].disabled=true;
    $("#form").submit();
}

var modalOriginal = document.getElementById("originalModal");

var imgOriginal = document.getElementById("originalImg");
var modalImgOriginal = document.getElementById("img01");


imgOriginal.onclick = function(){
    modalOriginal.style.display = "block";
    modalImgOriginal.src = this.src;
}

var originalCloseSpan = document.getElementById("originalClose");

var downloadOriginalSpan = document.getElementById("originalDownload");

originalCloseSpan.onclick = function() {
    modalOriginal.style.display = "none";
}


downloadOriginalSpan.onclick = function () {
    window.open($("#originalDownload").attr('value'));
}


