package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import com.google.api.client.util.DateTime;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.core.Credentials;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class StorageServiceImplementation implements StorageService {
    @Autowired
    private Environment env;

    //private Credentials credentials;
    private UUID uuid;

    @PostConstruct
    public void init() throws IOException {
        //credentials = GoogleCredentials.fromStream(new FileInputStream(env.getProperty("google.credentials")));

    }

    @Override
    public String uploadAudio(MultipartFile uploadedFile) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = "generic_framework_audio_bucket";
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        String extension = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
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
    public String uploadImage() {
        return null;
    }
}
