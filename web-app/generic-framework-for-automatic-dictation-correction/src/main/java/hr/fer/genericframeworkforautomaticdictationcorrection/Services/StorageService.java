package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public interface StorageService {

    String uploadAudio(MultipartFile uploadedFile) throws IOException;

    String transcribeAudio(String url, String langCode) throws IOException;

    String uploadImage(MultipartFile uploadedFile) throws IOException;
}
