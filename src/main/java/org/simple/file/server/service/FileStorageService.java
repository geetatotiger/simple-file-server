package org.simple.file.server.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    String storeFileOnServer(MultipartFile file);
    Resource fetchFile(String filename);
    List<String> listFiles() ;
}
