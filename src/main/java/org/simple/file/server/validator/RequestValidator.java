package org.simple.file.server.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

@Component
public class RequestValidator {

    private static final String FILENAME_PATTERN = "[a-zA-Z0-9_.-]+";
    private Pattern pattern = Pattern.compile(FILENAME_PATTERN);
    private static final int MIN_FILENAME_LENGTH = 2;
    private static final int MAX_FILENAME_LENGTH = 255;

    public void validateUploadRequest(MultipartFile file){
        String filename = file.getOriginalFilename();

        validateFilename(filename);

        if(file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

    }

    public void validateFilename(String filename) {
        if(!isFilenameValid(filename)) {
            throw new IllegalArgumentException("Invalid file name. File name can contain only alphanumeric, underscores,dot and hyphens");
        }

        if(!isFileNameSizeWithinLimits(filename)) {
            throw new IllegalArgumentException("File name length must be between 1 to 255 chars");
        }
    }

    private Boolean isFilenameValid(String filename) {
        return (filename != null) && pattern.matcher(filename).matches();
    }

    private Boolean isFileNameSizeWithinLimits(String filename){
        return filename.length() >= MIN_FILENAME_LENGTH && filename.length() <= MAX_FILENAME_LENGTH;
    }

}
