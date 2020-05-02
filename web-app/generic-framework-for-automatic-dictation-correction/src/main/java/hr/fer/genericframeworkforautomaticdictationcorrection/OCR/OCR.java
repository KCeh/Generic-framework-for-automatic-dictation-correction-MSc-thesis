package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

public interface OCR {
    public String detectText(String imageUrl);

    //public File drawBoundBoxesForIncorrectWords(String originalImageUrl, List<List<Integer>> boundBoxCoordinates)

    public String getName();
}
