package org.simple.file.server.controller;

import org.simple.file.server.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdviceHandler {

    Logger logger = LoggerFactory.getLogger(ControllerAdviceHandler.class);

    @ExceptionHandler(
            {IllegalArgumentException.class, FileAlreadyPresentException.class,
                    FileDoNotPresetException.class})
    public ResponseEntity<String> handleIllegalArgumentException(Exception exception) {
        logger.error(" Exception: ", exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(
            {
                    FileUploadException.class, FileDownloadException.class,
                    NotADirectoryException.class, DirectoryIOException.class,
                    PathInvalidException.class, FileUrlException.class,
                    ListingFileException.class, FilePermissionException.class,
                    DeleteFileException.class, StorageException.class
            })
    public ResponseEntity<String> handleAllPossibleFileExceptions(Exception exception) {
        logger.error("Exception while file operation ", exception);
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> UnhandledException(Exception exception) {
        logger.error("Unhandled exception occurred in software", exception);
        return ResponseEntity.internalServerError().body("File server not responding at the moment please try again after some time");
    }
}
