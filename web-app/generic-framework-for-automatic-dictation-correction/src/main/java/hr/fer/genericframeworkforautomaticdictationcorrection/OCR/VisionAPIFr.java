package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class VisionAPIFr implements OCR {
    private final String NAME = "Vision API - Français";


    @Override
    public String detectText(String imageUrl) throws IOException {
        //delete for deployment
        FileInputStream credentialsStream = new FileInputStream("C:/FER/MSc-thesis-test-web-app-433f4665a799.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

        ImageAnnotatorSettings ias = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .build();

        //url to storage uri
        String[] parts= imageUrl.split("/");
        String filename = parts[parts.length-1];
        filename=filename.split("\\?")[0]; //check url in db to understand
        String uri="gs://generic_framework_image_bucket/"+filename;

        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("fr").build();


        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(uri).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).setImageContext(imageContext).build();
        requests.add(request);


        StringBuilder stringBuilder = new StringBuilder();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(ias)) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();



            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    return  res.getError().getMessage();
                }

                List<EntityAnnotation> annotationList = res.getTextAnnotationsList();
                int i=0;
                for (EntityAnnotation annotation : annotationList) {
                    i++;
                    if(i==1) continue;//we want words, so skip whole text
                    stringBuilder.append(annotation.getDescription());
                    stringBuilder.append(" ");
                    // out.printf("Position : %s\n", annotation.getBoundingPoly());
                }
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
