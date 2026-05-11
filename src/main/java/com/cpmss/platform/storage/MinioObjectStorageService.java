package com.cpmss.platform.storage;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

/**
 * MinIO implementation of {@link ObjectStorageService}.
 */
@Service
public class MinioObjectStorageService implements ObjectStorageService {

    private final MinioClient minioClient;
    private final MinioStorageProperties properties;

    /**
     * Constructs the MinIO storage adapter.
     *
     * @param minioClient configured MinIO client
     * @param properties  storage properties
     */
    public MinioObjectStorageService(MinioClient minioClient,
                                     MinioStorageProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public void putObject(String objectKey, InputStream inputStream,
                          long sizeBytes, String contentType) {
        try {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .stream(inputStream, sizeBytes, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception ex) {
            throw new ApiException(CommonErrorCode.FILE_STORAGE_FAILURE);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(properties.bucket())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(properties.bucket())
                    .build());
        }
    }

    @Override
    public void deleteObject(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception ex) {
            throw new ApiException(CommonErrorCode.FILE_STORAGE_FAILURE);
        }
    }

    @Override
    public String createDownloadUrl(String objectKey, String downloadFilename,
                                    Duration expiry) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .expiry((int) expiry.toSeconds())
                    .extraQueryParams(Map.of(
                            "response-content-disposition",
                            "attachment; filename=\"" + downloadFilename + "\""))
                    .build());
        } catch (Exception ex) {
            throw new ApiException(CommonErrorCode.FILE_STORAGE_FAILURE);
        }
    }
}
