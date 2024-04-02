package org.simple.file.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileServerController {

    Logger logger = LoggerFactory.getLogger(FileServerController.class);

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file) {
        logger.debug("Got request to upload file: {}", file.getOriginalFilename());
        if(file.isEmpty()) {
            logger.error("Failed to upload file: {} as file is empty", file.getOriginalFilename());
            return ResponseEntity.badRequest().body("Empty file failed to upload");
        }
        //TODO: Add service to do actual work
        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());

    }
}
