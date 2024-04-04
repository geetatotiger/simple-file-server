package org.simple.file.server.persistence;

public enum StorageErrorCode {
    FILE_SAVE_FAILED,
    FILE_ALREADY_EXISTS,
    FILE_DO_NOT_EXIST,
    NOT_A_DIRECTORY,
    DIRECTORY_IO_ERROR,
    DIRECTORY_PERMISSION_ERROR,
    INVALID_PATH,
    FILE_URL_ERROR
}
