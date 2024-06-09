package mariposas.service.impl;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Singleton;
import mariposas.config.S3Config;
import mariposas.exception.BaseException;
import mariposas.service.S3Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static mariposas.constant.AppConstant.BUCKET_PATH;
import static mariposas.constant.AppConstant.IMAGE_NAME;
import static mariposas.constant.AppConstant.SAVE_IMAGE_ERROR;

@Singleton
public class S3ServiceImpl implements S3Service {
    private final S3Config s3Config;
    private final String bucketName;

    public S3ServiceImpl(S3Config s3Config, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Config = s3Config;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(String name, CompletedFileUpload image) throws IOException {
        var filename = String.format(IMAGE_NAME, name);
        var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(BUCKET_PATH + filename)
                .build();

        var response = s3Config.s3Client().putObject(putObjectRequest, RequestBody.fromBytes(image.getBytes()));

        if (response != null && !response.sdkHttpResponse().isSuccessful()) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, SAVE_IMAGE_ERROR);
        }

        return filename;
    }
}