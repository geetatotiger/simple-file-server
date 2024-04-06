package org.simple.file.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.simple.file.server.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerAdviceHandler {
    @ExceptionHandler(value = {
            IllegalArgumentException.class, FileAlreadyPresentException.class})
    public ResponseEntity<String> handleIllegalArgumentException(Exception exception) {
        log.error("Exception: ", exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(FileDoNotPresetException.class)
    public ResponseEntity<String> fileNotFoundExceptionHandle(Exception e) {
        log.error("File not found",e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    @ExceptionHandler(
            value = {
                    FileUploadException.class, FileDownloadException.class,
                    NotADirectoryException.class, DirectoryIOException.class,
                    PathInvalidException.class, FileUrlException.class,
                    ListingFileException.class, FilePermissionException.class,
                    DeleteFileException.class, StorageException.class
            })
    public ResponseEntity<String> handleAllPossibleFileExceptions(Exception exception) {
        log.error("Exception while file operation ", exception);
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> UnhandledException(Exception exception) {
        log.error("Unhandled exception occurred in software", exception);
        return ResponseEntity.internalServerError().body("File server could not complete the request");
    }
}
