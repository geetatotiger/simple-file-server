package org.simple.file.server.service;

import org.simple.file.server.exception.StorageException;
import org.simple.file.server.exception.StorageFileUploadException;
import org.simple.file.server.persistence.StorageError;
import org.simple.file.server.persistence.StorageErrorCode;
import org.simple.file.server.persistence.StorageSystem;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorageServiceImpl implements FileStorageService {

    private final StorageSystem storageSystem;

    public FileStorageServiceImpl(StorageSystem storageSystem) {
        this.storageSystem = storageSystem;
    }

    @Override
    public URI storeFileOnServer(MultipartFile file) {
        return storageSystem.saveFile(file)
                .fold(storageError -> {
                            throw mapStorageErrorToException(storageError);
                        },
                        filePath -> filePath
                );

    }

    @Override
    public Resource fetchFile(String filename) {
        return null;
    }

    @Override
    public List<String> listFiles() {
        return null;
    }

    private StorageException mapStorageErrorToException(StorageError storageError){
        Map<StorageErrorCode, StorageException> exceptionMap = new HashMap<>();
        exceptionMap.put(StorageErrorCode.FILE_SAVE_FAILED, new StorageFileUploadException(storageError.message()));
        return exceptionMap.getOrDefault(storageError.code(), new StorageException(storageError.message()));
    }
}
