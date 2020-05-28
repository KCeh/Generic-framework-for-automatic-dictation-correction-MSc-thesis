package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.OCRException;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

@Component
public class CustomOCREng implements OCR {
    private final String NAME = "Custom OCR - English (experimental, not recommended)";

    @Override
    public String detectText(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);

        String[] parts = imageUrl.split("/");
        String filename = parts[parts.length - 1];
        filename = filename.split("\\?")[0]; //check url in db to understand
        String extension = filename.split("\\.")[1];

        String address = "https://msc-thesis-test-web-app.ew.r.appspot.com/api/ocr";
        URL apiUrl = new URL(address);
        HttpURLConnection con = (HttpURLConnection) apiUrl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "image/" + extension);

        con.setDoOutput(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, extension, baos);
        baos.flush();

        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.write(baos.toByteArray());
        out.flush();
        out.close();
        baos.close();

        int status = con.getResponseCode();

        if (status > 299)
            throw new OCRException("Error while making API call");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inline = "";
        Scanner sc = new Scanner(br);
        while (sc.hasNext()) {
            inline += sc.nextLine();
        }
        sc.close();
        br.close();

        JSONParser parse = new JSONParser();
        JSONObject jobj = (JSONObject) parse.parse(inline);

        String result = (String) jobj.get("text");

        if (result.contains("Unable to process.")) throw new OCRException("API call couldn't return result");

        return result;
    }



    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getHTMLDiff(String originalText, String detectedText) {
        detectedText=detectedText.trim().replaceAll("\n", " ").replaceAll("\r", " ")
                .replaceAll("\\.", " ").replaceAll("\\?", " ")
                .replaceAll("\\!", " ").replaceAll(",", " ");

        originalText = originalText.trim().replaceAll("\n", " ").replaceAll("\r", " ")
                .replaceAll("\\.", " ").replaceAll("\\?", " ")
                .replaceAll("\\!", " ").replaceAll(",", " ");
        String[] originalWords = originalText.split("\\s+");
        String[] detectedWords = detectedText.split("\\s+");

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        LinkedList<DiffMatchPatch.Diff> diff = diffMatchPatch.diffMain(String.join(" ",originalWords), String.join(" ",detectedWords));
        String htmlDiff = diffMatchPatch.diffPrettyHtml(diff);

        htmlDiff=htmlDiff.replaceAll("#ffe6e6","#ff564a").replaceAll("#e6ffe6","#ff564a");

        return htmlDiff;
    }

    @Override
    public MultipartFile drawBoundBoxesForIncorrectWords(String originalImageUrl, String originalText, String
            detectedText) throws IOException {
        return null;
    }
}
