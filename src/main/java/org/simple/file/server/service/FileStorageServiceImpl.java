package org.simple.file.server.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileStorageServiceImpl implements FileStorageService{


    @Override
    public String storeFileOnServer(MultipartFile file) {
        return null;
    }

    @Override
    public Resource fetchFile(String filename) {
        return null;
    }

    @Override
    public List<String> listFiles() {
        return null;
    }
}
