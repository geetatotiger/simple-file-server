package org.simple.file.server.service;

import org.simple.file.server.exception.*;
import org.simple.file.server.persistence.StorageError;
import org.simple.file.server.persistence.StorageErrorCode;
import org.simple.file.server.persistence.StorageSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorageServiceImpl implements FileStorageService {

    private final StorageSystem storageSystem;

    public FileStorageServiceImpl(StorageSystem storageSystem) {
        this.storageSystem = storageSystem;
    }

    Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Override
    public URI storeFileOnServer(MultipartFile file) {
        return storageSystem.saveFile(file)
                .fold(storageError -> {
                            logger.error("Could not save the file on persistence store {}", file.getOriginalFilename());
                            throw mapStorageErrorToException(storageError);
                        },
                        filePath -> filePath
                );

    }

    @Override
    public Resource fetchFile(String filename) {
        return storageSystem.fetchFile(filename)
                .fold(
                        storageError -> {
                            logger.error("Could not get the file from storage {}", filename);
                            throw mapStorageErrorToException(storageError);
                        },
                        resource -> resource
                );

    }

    @Override
    public List<String> listFiles() {
        return storageSystem.listFiles()
                .fold(
                        storageError -> {
                            logger.error("Failed to list files uploaded");
                            throw mapStorageErrorToException(storageError);
                        },
                        listOfFiles -> listOfFiles
                );
    }

    @Override
    public Boolean fileDelete(String filename) {
        return storageSystem.deleteFile(filename)
                .fold(storageError -> {
                            logger.error("Failed to delete file");
                            throw mapStorageErrorToException(storageError);
                        },
                        deleted -> deleted
                );
    }

    private StorageException mapStorageErrorToException(StorageError storageError) {
        Map<StorageErrorCode, StorageException> exceptionMap = new HashMap<>();
        exceptionMap.put(StorageErrorCode.FILE_SAVE_FAILED, new FileUploadException(storageError.message()));
        exceptionMap.put(StorageErrorCode.FILE_ALREADY_EXISTS, new FileAlreadyPresentException(storageError.message()));
        exceptionMap.put(StorageErrorCode.FILE_DO_NOT_EXIST, new FileDoNotPresetException(storageError.message()));
        exceptionMap.put(StorageErrorCode.FILE_DOWNLOAD_FAILED, new FileDownloadException(storageError.message()));
        exceptionMap.put(StorageErrorCode.NOT_A_DIRECTORY, new NotADirectoryException(storageError.message()));
        exceptionMap.put(StorageErrorCode.DIRECTORY_IO_ERROR, new DirectoryIOException(storageError.message()));
        exceptionMap.put(StorageErrorCode.INVALID_PATH, new PathInvalidException(storageError.message()));
        exceptionMap.put(StorageErrorCode.FILE_URL_ERROR, new FileUrlException(storageError.message()));
        exceptionMap.put(StorageErrorCode.FILE_LIST_FAILED, new ListingFileException(storageError.message()));
        exceptionMap.put(StorageErrorCode.FILE_PERMISSION_ERROR, new FilePermissionException(storageError.message()));
        exceptionMap.put(StorageErrorCode.FILE_DELETE_FAILED, new DeleteFileException(storageError.message()));
        return exceptionMap.getOrDefault(storageError.code(), new StorageException(storageError.message()));
    }
}
