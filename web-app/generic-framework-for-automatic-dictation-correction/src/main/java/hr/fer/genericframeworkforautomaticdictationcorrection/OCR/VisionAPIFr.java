package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import com.google.cloud.vision.v1.ImageContext;
import org.springframework.stereotype.Component;

@Component
public class VisionAPIFr extends VisionAPI {
    private final String NAME = "Vision API - Français";


    @Override
    public ImageContext getLanguageConfig() {
        return ImageContext.newBuilder().addLanguageHints("fr").build();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
