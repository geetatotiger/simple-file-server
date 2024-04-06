package org.simple.file.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Upolad a file", description = "This API endpoint allows you to upload a file.")
    @ApiResponse(responseCode = "201", description = "File uploaded successfully",
            headers = {
                    @Header(name = "Location", description = "The URI of the created resource", schema = @Schema(type = "string"))
            })
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file) {
        log.debug("Got request to upload file: {}", file.getOriginalFilename());

        requestValidator.validateUploadRequest(file);
        URI fileURI = fileStorageService.storeFileOnServer(file);

        return ResponseEntity.created(fileURI).body("File uploaded successfully");

    }

    @GetMapping("/file/{filename}")
    @Operation(summary = "Download a file", description = "This API endpoint allows you to download a file.")
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
    @Operation(summary = "List all files", description = "This API endpoint returns a list of all file names.")
    public ResponseEntity<List<String>> listFiles() {
        log.debug("Got request to list the files");
        List<String> fileList = fileStorageService.listFiles();
        return ResponseEntity.ok(fileList);
    }

    @DeleteMapping("/file/{filename}")
    @Operation(summary = "Delete a file", description = "This API endpoint allows you to delete a file.")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        log.debug("Got request to delete file with name : {}", filename);

        requestValidator.validateFilename(filename);
        fileStorageService.fileDelete(filename);

        return ResponseEntity.ok()
                .body("File deleted successfully " + filename);
    }
}
