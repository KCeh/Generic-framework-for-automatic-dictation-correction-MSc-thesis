package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.*;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class StorageServiceImplementation implements StorageService {
    @Autowired
    private Environment env;


    @Override
    public String uploadAudio(MultipartFile uploadedFile) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = "generic_framework_audio_bucket";
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        String extension = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
        //microphone recorded
        if(extension.isEmpty()){
            extension="wav";
        }
        String fileName = randomUUIDString+'.'+extension;

        BlobInfo blobInfo =
                storage.create(
                        BlobInfo
                                .newBuilder(bucketName, fileName)
                                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                                .build()
                        , uploadedFile.getBytes()
                );
        System.out.println(blobInfo.getMediaLink());
        return blobInfo.getMediaLink();
    }

    @Override
    public String transcribeAudio(String url, String langCode) throws IOException {
        //delete for deployment
        FileInputStream credentialsStream = new FileInputStream("C:/FER/MSc-thesis-test-web-app-433f4665a799.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
        SpeechSettings speechSettings =
                SpeechSettings.newBuilder()
                        .setCredentialsProvider(credentialsProvider)
                        .build();

        SpeechClient speechClient = SpeechClient.create(speechSettings);

        //SpeechClient speechClient = SpeechClient.create();

        // Sample rate in Hertz of the audio data sent
        //int sampleRateHertz = 16000;

        //URI not URL
        //https://storage.cloud.google.com/generic_framework_audio_bucket/a4213f9d-d3c6-4278-9874-66da9147c833.wav
        //url = "gs://generic_framework_audio_bucket/a4213f9d-d3c6-4278-9874-66da9147c833.wav";
        String[] parts= url.split("/");
        String filename = parts[parts.length-1];
        filename=filename.split("\\?")[0]; //check url in db to understand
        String uri="gs://generic_framework_audio_bucket/"+filename;

        String extension = filename.split("\\.")[1];

        RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.LINEAR16;
        if(extension.toLowerCase().equals("flac"))
            encoding = RecognitionConfig.AudioEncoding.FLAC;

        // Encoding of audio data sent. This sample sets this explicitly.
        // This field is optional for FLAC and WAV audio formats.
        // make sure audio is stereo

        RecognitionConfig config =
                RecognitionConfig.newBuilder()
                        //.setSampleRateHertz(sampleRateHertz)
                        .setLanguageCode(langCode)
                        .setEncoding(encoding)
                        .setAudioChannelCount(2)
                        .setEnableSeparateRecognitionPerChannel(false)
                        .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(uri).build();
        RecognizeRequest request =
                RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
        RecognizeResponse response = speechClient.recognize(request);

        StringBuilder stringBuilder = new StringBuilder();
        for (SpeechRecognitionResult result : response.getResultsList()) {
            // First alternative is the most probable result
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            stringBuilder.append(alternative.getTranscript());
        }
        return stringBuilder.toString();
    }

    @Override
    public String uploadImage(MultipartFile uploadedFile) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = "generic_framework_image_bucket";
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        String extension = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());

        if(extension.equals(""))
            extension="jpg";

        String fileName = randomUUIDString+'.'+extension;

        BlobInfo blobInfo =
                storage.create(
                        BlobInfo
                                .newBuilder(bucketName, fileName)
                                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                                .build()
                        , uploadedFile.getBytes()
                );
        System.out.println(blobInfo.getMediaLink());
        return blobInfo.getMediaLink();
    }

    @Override
    public void deleteAudio(String url) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = "generic_framework_audio_bucket";

        String[] parts= url.split("/");
        String filename = parts[parts.length-1];
        filename=filename.split("\\?")[0]; //check url in db to understand

        storage.delete(bucketName, filename);
    }

    @Override
    public void deleteImage(String url) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = "generic_framework_image_bucket";

        String[] parts= url.split("/");
        String filename = parts[parts.length-1];
        filename=filename.split("\\?")[0]; //check url in db to understand

        storage.delete(bucketName, filename);
    }

}
