package org.simple.file.server.persistence;

import io.vavr.control.Either;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

public interface StorageSystem {

    Either<StorageError, URI> saveFile(MultipartFile file);
    Either<StorageError, Resource> fetchFile(String filename);

    Either<StorageError, List<String>> listFiles();
}
