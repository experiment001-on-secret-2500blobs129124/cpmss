package com.cpmss.platform.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO connection settings for binary object storage.
 *
 * @param endpoint  S3-compatible endpoint URL
 * @param accessKey MinIO/S3 access key
 * @param secretKey MinIO/S3 secret key
 * @param bucket    bucket used by CPMSS file workflows
 */
@ConfigurationProperties(prefix = "storage.minio")
public record MinioStorageProperties(
        String endpoint,
        String accessKey,
        String secretKey,
        String bucket
) {
    /**
     * Applies safe local defaults for development and tests.
     */
    public MinioStorageProperties {
        endpoint = blankToDefault(endpoint, "http://localhost:9000");
        accessKey = blankToDefault(accessKey, "minioadmin");
        secretKey = blankToDefault(secretKey, "minioadmin");
        bucket = blankToDefault(bucket, "cpmss-files");
    }

    private static String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
