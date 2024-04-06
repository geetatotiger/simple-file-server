package org.simple.file.server.persistence.localstore;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.simple.file.server.persistence.StorageError;
import org.simple.file.server.persistence.StorageErrorCode;
import org.simple.file.server.persistence.StorageSystem;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class LocalFileStorage implements StorageSystem {
    private String uploadDir = null;
    private Path uploadFolderOnServer = null;

    public LocalFileStorage(String uploadDir) {
        try {
            this.uploadDir = uploadDir;
            this.uploadFolderOnServer = Path.of(uploadDir);
            if (!Files.exists(uploadFolderOnServer)) {
                Files.createDirectory(uploadFolderOnServer);
            }
        } catch (InvalidPathException e) {
            log.error("given upload directory path can not be created", e);
            System.exit(-1);
        } catch (Exception e) {
            log.error("exception while creating upload directory", e);
            System.exit(-1);
        }
    }

    @Override
    public Either<StorageError, URI> saveFile(MultipartFile file) {
        {
            try {
                Path targetLocation = uploadFolderOnServer.resolve(file.getOriginalFilename());

                if (Files.exists(targetLocation)) {
                    log.error("File already exists. Can not upload file : {}", file.getOriginalFilename());
                    return Either.left(new StorageError(StorageErrorCode.FILE_ALREADY_EXISTS,
                            "File with name:" + file.getOriginalFilename() + " already exists, can not upload file with same name"));
                }

                file.transferTo(targetLocation);
                return Either.right(targetLocation.toUri());

            } catch (Exception e) {
                log.error("Exception while saving file : {}", file.getOriginalFilename(), e);
                return Either.left(new StorageError(StorageErrorCode.FILE_SAVE_FAILED, "exception while copying file"));
            }
        }
    }

    @Override
    public Either<StorageError, Resource> fetchFile(String filename) {
        try {
            Path path = Paths.get(uploadDir, filename);
            if(!Files.exists(path)) {
                return Either.left(new StorageError(StorageErrorCode.FILE_DO_NOT_EXIST, "File do not present " + filename));
            }
            return Either.right(new UrlResource(path.toUri()));

        } catch (InvalidPathException e) {
            log.error("File path is not proper {}", filename, e);
            return Either.left(new StorageError(StorageErrorCode.INVALID_PATH, "File do not present" + filename));
        } catch (MalformedURLException e) {
            log.error("Malformed URI exception {}", filename, e);
            return Either.left(new StorageError(StorageErrorCode.FILE_URL_ERROR, "File URL creation error" + filename));
        } catch (Exception e) {
            log.error("File download failed {}", filename, e);
            return Either.left(new StorageError(StorageErrorCode.FILE_DOWNLOAD_FAILED, "File download failed"));
        }
    }

    @Override
    public Either<StorageError, List<String>> listFiles() {
        try (Stream<Path> stream = Files.list(uploadFolderOnServer)) {
            return Either.right(stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList());
        } catch (SecurityException e) {
            log.error("Directory read exception {}", uploadDir, e);
            return Either.left(new StorageError(StorageErrorCode.DIRECTORY_PERMISSION_ERROR, "Upload directory do not have read permission"));
        } catch (NotDirectoryException e) {
            log.error("Trying to read from the folder which is not directory {}", uploadDir, e);
            return Either.left(new StorageError(StorageErrorCode.NOT_A_DIRECTORY, "Trying to list files from folder which is not directory"));
        } catch (IOException e) {
            log.error("IO error while listing files from upload directory {}", uploadDir, e);
            return Either.left(new StorageError(StorageErrorCode.DIRECTORY_IO_ERROR, "IO error while listing files"));
        } catch (Exception e) {
            log.error("Exception while downloading file");
            return Either.left(new StorageError(StorageErrorCode.FILE_LIST_FAILED, "listing uploaded file failed"));
        }
    }

    @Override
    public Either<StorageError, Boolean> deleteFile(String filename) {
        try {
            Path path = Paths.get(uploadDir, filename);
            if(!Files.exists(path)) {
                return Either.left(new StorageError(StorageErrorCode.FILE_DO_NOT_EXIST, "File do not present " + filename));
            }
            Files.delete(path);
            return Either.right(true);
        } catch (SecurityException e) {
            log.error("No permission to delete file {}", filename);
            return Either.left(new StorageError(StorageErrorCode.FILE_PERMISSION_ERROR, "No permission to delete file" + filename));
        } catch (NoSuchFileException e) {
            log.error("No such directory exist to delete {}", filename);
            return Either.left(new StorageError(StorageErrorCode.FILE_DO_NOT_EXIST, "No such file found to delete" + filename));
        } catch (IOException e) {
            log.error("File delete failed on IO error {}", filename);
            return Either.left(new StorageError(StorageErrorCode.FILE_DELETE_FAILED, "File delete failed" + filename));
        } catch (Exception e) {
            log.error("File delete failed {}", filename);
            return Either.left(new StorageError(StorageErrorCode.FILE_DELETE_FAILED, "File delete failed" + filename));
        }

}
}
