package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface OCR {

    //https://cloud.google.com/vision/docs/supported-files
    //https://stackoverflow.com/questions/13854343/which-format-of-images-are-supported-by-opencv

    public String detectText(String imageUrl) throws IOException;

    public String getName();

    public MultipartFile drawBoundBoxesForIncorrectWords(String originalImageUrl, String originalText, String detectedText) throws IOException;
}
