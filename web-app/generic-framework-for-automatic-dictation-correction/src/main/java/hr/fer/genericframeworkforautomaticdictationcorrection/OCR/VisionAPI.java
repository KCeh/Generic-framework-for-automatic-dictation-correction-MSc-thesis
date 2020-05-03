package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class VisionAPI implements OCR {
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
        String[] parts = imageUrl.split("/");
        String filename = parts[parts.length - 1];
        filename = filename.split("\\?")[0]; //check url in db to understand
        String uri = "gs://generic_framework_image_bucket/" + filename;

        List<AnnotateImageRequest> requests = new ArrayList<>();


        ImageContext imageContext = getLanguageConfig();


        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(uri).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build(); //DOCUMENT_TEXT_DETECTION -> supports handwriting
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).setImageContext(imageContext).build();
        requests.add(request);


        String result="";

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(ias)) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            client.close();


            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    String msg = res.getError().getMessage();
                    System.err.println("Error");
                    System.err.println(msg);
                    return msg;
                }

                TextAnnotation annotation = res.getFullTextAnnotation();
                for (Page page : annotation.getPagesList()) {
                    String pageText = "";
                    for (Block block : page.getBlocksList()) {
                        String blockText = "";
                        for (Paragraph para : block.getParagraphsList()) {
                            String paraText = "";
                            for (Word word : para.getWordsList()) {
                                String wordText = "";
                                //get bound boxes for words
                                for (Symbol symbol : word.getSymbolsList()) {
                                    wordText = wordText + symbol.getText();
                                };
                                paraText = String.format("%s %s", paraText, wordText);
                            }
                            // Output Example using Paragraph:
                            blockText = blockText + paraText;
                        }
                        pageText = pageText + blockText;
                    }
                }
                result=annotation.getText();
            }
        }

        return result;
    }

    public abstract ImageContext getLanguageConfig();

    @Override
    public String getName() {
        return "";
    }


}
