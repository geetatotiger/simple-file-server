package org.simple.file.server.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

public interface FileStorageService {
    URI storeFileOnServer(MultipartFile file);
    Resource fetchFile(String filename);
    List<String> listFiles() ;

    Boolean fileDelete(String filename);
}
