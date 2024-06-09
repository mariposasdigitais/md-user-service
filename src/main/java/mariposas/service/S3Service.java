package mariposas.service;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.io.IOException;

public interface S3Service {
    String uploadFile(String name, CompletedFileUpload image) throws IOException;
}