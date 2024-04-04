package org.simple.file.server.controller;

import org.simple.file.server.service.FileStorageService;
import org.simple.file.server.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
public class FileServerController {

    Logger logger = LoggerFactory.getLogger(FileServerController.class);
    private final RequestValidator requestValidator;
    private final FileStorageService fileStorageService;

    @Autowired
    public FileServerController(RequestValidator requestValidator,
                                FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.requestValidator = requestValidator;
    }

    @PostMapping("/upload")
    public ResponseEntity<URI> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.debug("Got request to upload file: {}", file.getOriginalFilename());

        requestValidator.validateUploadRequest(file);
        URI fileURI = fileStorageService.storeFileOnServer(file);

        return ResponseEntity.created(fileURI).build();

    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        logger.debug("Got request to download file with name : {}", filename);

        requestValidator.validateFilename(filename);
        Resource resource = fileStorageService.fetchFile(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                + filename +"\"")
                .body(resource);
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> listFiles() {
        logger.debug("Got request to list the files");
        List<String> fileList = fileStorageService.listFiles();
        return ResponseEntity.ok(fileList);
    }
}
