package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Image;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class VisionAPI implements OCR {
    private List<BoundingPoly> boundingPolies;

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


        String result = "";
        boundingPolies = new ArrayList<>();

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
                    for (Block block : page.getBlocksList()) {
                        for (Paragraph para : block.getParagraphsList()) {
                            for (Word word : para.getWordsList()) {
                                //get bound boxes for words
                                List<Symbol> symbols = word.getSymbolsList();
                                if (symbols.size() == 1) {
                                    String symbol = symbols.get(0).getText();
                                    if (symbol.equals(".") || symbol.equals("!") || symbol.equals("?") || symbol.equals(","))
                                        continue;
                                }
                                boundingPolies.add(word.getBoundingBox());
                            }
                            ;
                        }
                    }
                }
                result = annotation.getText();
            }
        }

        return result;
    }

    public abstract ImageContext getLanguageConfig();

    @Override
    public String getName() {
        return "";
    }

    public MultipartFile drawBoundBoxesForIncorrectWords(String originalImageUrl, String originalText, String detectedText) throws IOException {
        List<List<Integer>> words = translatePoliesToCoordinates();

        originalText = originalText.trim().replaceAll("\n", " ").replaceAll("\r", " ")
                .replaceAll("\\.", " ").replaceAll("\\?", " ")
                .replaceAll("\\!", " ").replaceAll(",", " ");
        detectedText = detectedText.trim().replaceAll("\n", " ").replaceAll("\r", " ")
                .replaceAll("\\.", " ").replaceAll("\\?", " ")
                .replaceAll("\\!", " ").replaceAll(",", " ");

        String[] originalWords = originalText.split("\\s+");
        String[] detectedWords = detectedText.split("\\s+");

        List<Integer> indexes = new ArrayList<>();
        int originalLen = originalWords.length;
        int detectedLen = detectedWords.length;

        int lengthDiff = detectedLen - originalLen;


        if (lengthDiff == 0) {
            for (int i = 0; i < originalLen; i++) {
                if (!originalWords[i].equals(detectedWords[i])) {
                    indexes.add(i);
                }

            }
        } else if (lengthDiff > 0) {
            int i = 0;
            int j = 0;
            boolean reset;
            while (i < originalLen && j < detectedLen) {
                if (!originalWords[i].equals(detectedWords[j])) {
                    indexes.add(j);
                    int oldJ = j;
                    reset = true;
                    while (j - i < detectedLen && j < detectedLen - 1) {
                        j++;
                        if (originalWords[i].equals(detectedWords[j])) {
                            reset = false;
                            for (int x = oldJ; x < j; x++)
                                indexes.add(x);
                            break;
                        }
                    }
                    if (reset) j = oldJ;
                }
                i++;
                j++;
            }
        } else { //improve
            for (int i = 0; i < originalLen; i++) {
                if (!originalWords[i].equals(detectedWords[i])) {
                    indexes.add(i);
                }
            }
        }


        BufferedImage img = null;
        //BufferedImage newImage = null;
        //handel grayscale and rgb differently?
        try {
            URL url = new URL(originalImageUrl);
            img = ImageIO.read(url);
            //newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(Color.RED);

            List<Integer> word;
            for (Integer index : indexes) {
                word = words.get(index);
                g2d.drawRect(word.get(0), word.get(1), word.get(2), word.get(3));
            }
            g2d.dispose();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        //extension..
        String[] parts = originalImageUrl.split("/");
        String filename = parts[parts.length - 1];
        filename = filename.split("\\?")[0];
        String extension = filename.split("\\.")[1];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, extension, baos);
        baos.flush();

        MultipartFile multipartFile = new MockMultipartFile(filename, baos.toByteArray());

        return multipartFile;
    }

    private List<List<Integer>> translatePoliesToCoordinates() {
        List<List<Integer>> words = new ArrayList<>();

        for (BoundingPoly poly : boundingPolies) {
            List<Integer> coordinatesForWord = new ArrayList<>();
            words.add(coordinatesForWord);

            List<Vertex> vertices = poly.getVerticesList();
            if (vertices.size() < 4) continue;
            int startX = vertices.get(0).getX();
            int startY = vertices.get(0).getY();

            int width = (vertices.get(1).getX() + vertices.get(2).getX()) / 2 - startX;
            int height = (vertices.get(2).getY() + vertices.get(3).getY()) / 2 - startY;

            if (width * height < 100) continue;
            coordinatesForWord.add(startX);
            coordinatesForWord.add(startY);
            coordinatesForWord.add(width);
            coordinatesForWord.add(height);
        }
        return words;
    }


}
