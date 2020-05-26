var modalOriginal = document.getElementById("originalModal");

var imgOriginal = document.getElementById("originalImg");
var modalImgOriginal = document.getElementById("img01");

var modalCorrected = document.getElementById("correctedModal");

var imgCorrected = document.getElementById("correctedImg");
var modalImgCorrected = document.getElementById("img02");

imgOriginal.onclick = function(){
    modalOriginal.style.display = "block";
    modalImgOriginal.src = this.src;
}

if(imgCorrected != null){
    imgCorrected.onclick = function(){
        modalCorrected.style.display = "block";
        modalImgCorrected.src = this.src;
    }
}


var originalCloseSpan = document.getElementById("originalClose");
var correctedCloseSpan = document.getElementById("correctedClose");

var downloadOriginalSpan = document.getElementById("originalDownload");
var downloadCorrectedSpan = document.getElementById("correctedDownload");

originalCloseSpan.onclick = function() {
    modalOriginal.style.display = "none";
}

if(correctedCloseSpan!=null){
    correctedCloseSpan.onclick = function() {
        modalCorrected.style.display = "none";
    }
}

downloadOriginalSpan.onclick = function () {
    window.open($("#originalDownload").attr('value'));
}

if(downloadCorrectedSpan!=null){
    downloadCorrectedSpan.onclick = function () {
        window.open($("#correctedDownload").attr('value'));
    }
}
