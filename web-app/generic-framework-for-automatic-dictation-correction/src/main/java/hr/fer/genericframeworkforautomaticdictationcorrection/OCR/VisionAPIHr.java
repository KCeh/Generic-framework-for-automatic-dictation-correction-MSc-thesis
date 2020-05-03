package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import com.google.cloud.vision.v1.ImageContext;
import org.springframework.stereotype.Component;

@Component
public class VisionAPIHr extends VisionAPI {
    private final String NAME = "Vision API - Hrvatski";

    @Override
    public ImageContext getLanguageConfig() {
        return ImageContext.newBuilder().addLanguageHints("hr").build();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
