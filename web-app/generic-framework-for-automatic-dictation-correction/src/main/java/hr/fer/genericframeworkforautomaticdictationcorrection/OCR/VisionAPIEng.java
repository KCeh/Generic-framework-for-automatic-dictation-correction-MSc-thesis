package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import com.google.cloud.vision.v1.ImageContext;
import org.springframework.stereotype.Component;

@Component
public class VisionAPIEng extends VisionAPI{
    private final String NAME = "Vision API - English";

    @Override
    public ImageContext getLanguageConfig() {
        return ImageContext.newBuilder().addLanguageHints("en").build();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
