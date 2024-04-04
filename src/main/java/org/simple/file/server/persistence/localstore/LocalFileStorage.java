package org.simple.file.server.persistence.localstore;

import io.vavr.control.Either;
import org.simple.file.server.persistence.StorageError;
import org.simple.file.server.persistence.StorageErrorCode;
import org.simple.file.server.persistence.StorageSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

public class LocalFileStorage implements StorageSystem {
    private String uploadDir = null;
    private Path uploadFolderOnServer = null;

    Logger logger = LoggerFactory.getLogger(LocalFileStorage.class);

    public LocalFileStorage(String uploadDir) {
        try {
            this.uploadDir = uploadDir;
            this.uploadFolderOnServer = Path.of(uploadDir);
            if (!Files.exists(uploadFolderOnServer)) {
                Files.createDirectory(uploadFolderOnServer);
            }
        } catch (InvalidPathException e) {
            logger.error("given upload directory path can not be created", e);
            System.exit(-1);
        } catch (Exception e) {
            logger.error("exception while creating upload directory", e);
            System.exit(-1);
        }
    }

    @Override
    public Either<StorageError, URI> saveFile(MultipartFile file) {
        {
            try {
                Path targetLocation = uploadFolderOnServer.resolve(file.getOriginalFilename());

                if(Files.exists(targetLocation) ) {
                    logger.error("File already exists. Can not upload file : {}", file.getOriginalFilename());
                    return Either.left(new StorageError(StorageErrorCode.FILE_ALREADY_EXISTS,
                            "File with name:" +file.getOriginalFilename() + " already exists, can not upload file with same name"));
                }

                file.transferTo(targetLocation);
                return Either.right(targetLocation.toUri());

            } catch (Exception e) {
                logger.error("Exception while saving file : {}", file.getOriginalFilename(),e);
                return Either.left(new StorageError(StorageErrorCode.FILE_SAVE_FAILED, "exception while copying file"));
            }
        }
    }

    @Override
    public Either<StorageError, Resource> fetchFile(String filename) {
        return null;
    }

    @Override
    public Either<StorageError, List<String>> listFiles() {
        return null;
    }
}
