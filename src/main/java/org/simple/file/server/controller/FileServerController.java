package org.simple.file.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.simple.file.server.service.FileStorageService;
import org.simple.file.server.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
public class FileServerController {

    private final RequestValidator requestValidator;
    private final FileStorageService fileStorageService;

    @Autowired
    public FileServerController(RequestValidator requestValidator,
                                FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.requestValidator = requestValidator;
    }

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.debug("Got request to upload file: {}", file.getOriginalFilename());

        requestValidator.validateUploadRequest(file);
        URI fileURI = fileStorageService.storeFileOnServer(file);

        return ResponseEntity.created(fileURI).body("File uploaded successfully");

    }

    @GetMapping("/file/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        log.debug("Got request to download file with name : {}", filename);

        requestValidator.validateFilename(filename);
        Resource resource = fileStorageService.fetchFile(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                + filename +"\"")
                .body(resource);
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> listFiles() {
        log.debug("Got request to list the files");
        List<String> fileList = fileStorageService.listFiles();
        return ResponseEntity.ok(fileList);
    }

    @DeleteMapping("/file/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        log.debug("Got request to delete file with name : {}", filename);

        requestValidator.validateFilename(filename);
        fileStorageService.fileDelete(filename);

        return ResponseEntity.ok()
                .body("File deleted successfully " + filename);
    }
}
