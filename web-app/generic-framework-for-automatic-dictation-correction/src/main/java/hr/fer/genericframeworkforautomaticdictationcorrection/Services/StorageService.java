package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String uploadAudio(MultipartFile uploadedFile) throws IOException;

    String uploadImage();
}
