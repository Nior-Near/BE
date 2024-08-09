package nior_near.server.global.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    Object upload(MultipartFile multipartFile, String directoryName) throws IOException;

    void remove(Object object) throws IOException;
}
