package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import com.google.cloud.vision.v1.ImageContext;
import org.springframework.stereotype.Component;

@Component
public class VisionAPIDe extends VisionAPI {
    private final String NAME = "Vision API - Deutsch";

    @Override
    public ImageContext getLanguageConfig() {
        return ImageContext.newBuilder().addLanguageHints("de").build();
    }

    @Override
    public String getName() {
        return NAME;
    }

}

