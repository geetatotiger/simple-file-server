package org.simple.file.server.exception;

import org.simple.file.server.persistence.StorageErrorCode;

public class FileAlreadyPresentException extends StorageException {
    public FileAlreadyPresentException(String message) {
        super(message);
    }
}
