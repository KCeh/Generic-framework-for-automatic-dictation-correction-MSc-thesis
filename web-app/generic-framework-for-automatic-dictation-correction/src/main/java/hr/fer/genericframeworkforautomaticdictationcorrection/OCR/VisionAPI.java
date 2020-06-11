package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Image;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

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
        //DOCUMENT_TEXT_DETECTION -> supports handwriting
        //use old model, May 15, 2020 -> new model ->works worse on test examples!? //june 30,2020 support will be dropped
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).setModel("builtin/legacy_20190601").build();
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
                                    if (symbol.equals(".") || symbol.equals("!") || symbol.equals("?")
                                            || symbol.equals(",")
                                            || symbol.equals("\"") || symbol.equals("\'") || symbol.equals("˝")
                                            || symbol.equals("\n") || symbol.equals("“") || symbol.equals("-"))
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

        result=result.trim().replaceAll("[^a-zA-Z0-9ČčĆćŠšĐđŽž]+"," ").replaceAll("\\s{2,}", " ");
        return result;
    }

    public abstract ImageContext getLanguageConfig();

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getHTMLDiff(String originalText, String detectedText) {
        originalText = originalText.trim().replaceAll("[^a-zA-Z0-9ČčĆćŠšĐđŽž]+"," ").replaceAll("\\s{2,}", " ");
        String[] originalWords = originalText.split("\\s+");
        String[] detectedWords = detectedText.split("\\s+");

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        LinkedList<DiffMatchPatch.Diff> diff = diffMatchPatch.diffMain(String.join(" ", detectedWords), String.join(" ", originalWords));
        String htmlDiff = diffMatchPatch.diffPrettyHtml(diff);

        htmlDiff = htmlDiff.replaceAll("#ffe6e6", "#ff564a").replaceAll("#e6ffe6", "#66ff66");
        return htmlDiff;
    }

    public MultipartFile drawBoundBoxesForIncorrectWords(String originalImageUrl, String originalText, String detectedText) throws IOException {
        List<List<Integer>> words = translatePoliesToCoordinates();
        originalText = originalText.trim().replaceAll("[^a-zA-Z0-9ČčĆćŠšĐđŽž]+"," ").replaceAll("\\s{2,}", " ");

        String[] originalWords = originalText.split("\\s+");
        String[] detectedWords = detectedText.split("\\s+");

        List<Integer> indexes = new ArrayList<>();
        int originalLen = originalWords.length;
        int detectedLen = detectedWords.length;

        Map<Integer, List<String>> inserted = new HashMap<>();
        Map<Integer, List<String>> deleted = new HashMap<>();

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        LinkedList<DiffMatchPatch.Diff> diff = diffMatchPatch.diffMain(String.join(" ", originalWords), String.join(" ", detectedWords));
        diffMatchPatch.diffCleanupSemantic(diff);

        for (int i = 0; i < detectedLen; i++) {
            indexes.add(i);
        }
        int lastIndex = -1;
        for (DiffMatchPatch.Diff part : diff) {

            if (part.operation.equals(DiffMatchPatch.Operation.EQUAL)) {
                String good = part.text.trim();
                String[] parts = good.split("\\s+");
                for (String word : parts) {
                    int index = ArrayUtils.indexOf(detectedWords, word, lastIndex);
                    if (index > -1) {
                        indexes.remove((Integer) index);
                        lastIndex = index;
                    }
                }
            }

            if (part.operation.equals(DiffMatchPatch.Operation.INSERT)) {
                String good = part.text.trim();
                String[] parts = good.split("\\s+");
                for (String word : parts) {
                    int index = ArrayUtils.indexOf(detectedWords, word, lastIndex);
                    if (index > -1) {
                        if (inserted.containsKey(index)) {
                            List<String> listOfCorrections = inserted.get(index);
                            listOfCorrections.add(word);
                        } else {
                            List<String> listOfCorrections = new ArrayList<>();
                            listOfCorrections.add(word);
                            inserted.put(index, listOfCorrections);
                        }
                    } else {
                        index=lastIndex;
                        if (inserted.containsKey(index + 1)) {
                            List<String> listOfCorrections = inserted.get(index + 1);
                            listOfCorrections.add(word);
                        } else {
                            List<String> listOfCorrections = new ArrayList<>();
                            listOfCorrections.add(word);
                            inserted.put(index + 1, listOfCorrections);
                        }
                    }
                }
            }

            if (part.operation.equals(DiffMatchPatch.Operation.DELETE)) {
                String good = part.text.trim();
                String[] parts = good.split("\\s+");
                for (String word : parts) {
                    int index = ArrayUtils.indexOf(detectedWords, word, lastIndex);
                    if (index > -1) {
                        if (deleted.containsKey(index)) {
                            List<String> listOfCorrections = deleted.get(index);
                            listOfCorrections.add(word);
                        } else {
                            List<String> listOfCorrections = new ArrayList<>();
                            listOfCorrections.add(word);
                            deleted.put(index, listOfCorrections);
                        }
                    } else {
                        index=lastIndex;
                        if(index>=0){
                            String newWord=detectedWords[index]+word;
                            if(ArrayUtils.indexOf(originalWords, newWord)>-1){
                                indexes.add(index);
                                if(index>0)
                                    index--;
                            }
                        }

                        if (deleted.containsKey(index + 1)) {
                            List<String> listOfCorrections = deleted.get(index + 1);
                            listOfCorrections.add(word);
                        } else {
                            List<String> listOfCorrections = new ArrayList<>();
                            listOfCorrections.add(word);
                            deleted.put(index + 1, listOfCorrections);
                        }
                    }
                }
            }

        }
        //}


        BufferedImage img = null;
        try {
            URL url = new URL(originalImageUrl);
            img = ImageIO.read(url);
            //newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3));

            int avrHeight = 0;

            List<Integer> word;
            for (Integer index : indexes) {
                word = words.get(index);
                if (word.size() < 4) continue;
                avrHeight += word.get(3);
                g2d.drawRect(word.get(0), word.get(1), word.get(2), word.get(3));
            }

            if (indexes.size() != 0)
                avrHeight /= indexes.size();
            else
                avrHeight = 50;
            avrHeight *= 0.4;

            //draw words
            g2d.setColor(Color.RED);
            int fontSize = avrHeight>50 ? 50 : avrHeight;
            //int fontSize = avrHeight;
            g2d.setFont(new Font("Arial Black", Font.PLAIN, fontSize));

            for (Map.Entry<Integer, List<String>> entry : inserted.entrySet()) {
                word = words.get(entry.getKey());
                if (word.size() < 4) continue;
                int i=0;
                for(String correct:entry.getValue()){
                    g2d.drawString(correct, word.get(0) + word.get(2) - 50 - i * fontSize, word.get(1) + word.get(3));
                    i++;
                }
            }

            g2d.setColor(Color.GREEN);


            for (Map.Entry<Integer, List<String>> entry : deleted.entrySet()) {
                word = words.get(entry.getKey());
                if (word.size() < 4) continue;
                int i=0;
                for(String correct:entry.getValue()){
                    g2d.drawString(correct, word.get(0) + i * fontSize, word.get(1) + word.get(3));
                    i++;
                }
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
