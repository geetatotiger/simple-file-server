package org.simple.file.server.service;

import lombok.extern.slf4j.Slf4j;
import org.simple.file.server.exception.*;
import org.simple.file.server.persistence.StorageError;
import org.simple.file.server.persistence.StorageErrorCode;
import org.simple.file.server.persistence.StorageSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final StorageSystem storageSystem;

    public FileStorageServiceImpl(StorageSystem storageSystem) {
        this.storageSystem = storageSystem;
    }

    @Override
    public URI storeFileOnServer(MultipartFile file) {
        return storageSystem.saveFile(file)
                .fold(storageError -> {
                            log.error("Could not save the file on persistence store {}", file.getOriginalFilename());
                            throw mapStorageErrorToException(storageError);
                        },
                        filePath -> {
                            log.info("File stored successfully at location : {} ", filePath);
                            return filePath;
                        }
                );
    }

    @Override
    public Resource fetchFile(String filename) {
        return storageSystem.fetchFile(filename)
                .fold(
                        storageError -> {
                            log.error("Could not get the file from storage {}", filename);
                            throw mapStorageErrorToException(storageError);
                        },
                        resource -> {
                            log.info("Successfully got the file from the store {}", filename);
                            return resource;
                        }
                );

    }

    @Override
    public List<String> listFiles() {
        return storageSystem.listFiles()
                .fold(
                        storageError -> {
                            log.error("Failed to list files uploaded");
                            throw mapStorageErrorToException(storageError);
                        },
                        listOfFiles -> {
                            log.info("Got the files list from the store");
                            return listOfFiles;
                        }
                );
    }

    @Override
    public void fileDelete(String filename) {
        storageSystem.deleteFile(filename)
                .fold(storageError -> {
                            log.error("Failed to delete file {}", filename);
                            throw mapStorageErrorToException(storageError);
                        },
                        deleted -> {
                            log.info("Deleted file {} successfully", filename);
                            return deleted;
                        }
                );
    }
    private StorageException mapStorageErrorToException(StorageError storageError){
        return switch (storageError.code()) {
            case FILE_SAVE_FAILED -> new FileUploadException(storageError.message());
            case FILE_ALREADY_EXISTS -> new FileAlreadyPresentException(storageError.message());
            case FILE_DO_NOT_EXIST ->  new FileDoNotPresetException(storageError.message());
            case FILE_DOWNLOAD_FAILED -> new FileDownloadException(storageError.message());
            case NOT_A_DIRECTORY -> new NotADirectoryException(storageError.message());
            case DIRECTORY_IO_ERROR -> new DirectoryIOException(storageError.message());
            case DIRECTORY_PERMISSION_ERROR -> null;
            case INVALID_PATH -> new PathInvalidException(storageError.message());
            case FILE_URL_ERROR -> new FileUrlException(storageError.message());
            case FILE_LIST_FAILED -> new ListingFileException(storageError.message());
            case FILE_PERMISSION_ERROR -> new FilePermissionException(storageError.message());
            case FILE_DELETE_FAILED -> new DeleteFileException(storageError.message());
            default -> new StorageException(storageError.message());
        };
    }
}
