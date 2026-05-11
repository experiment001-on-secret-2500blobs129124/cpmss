package com.cpmss.platform.storage;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires MinIO-backed object storage infrastructure.
 */
@Configuration
@EnableConfigurationProperties(MinioStorageProperties.class)
public class MinioStorageConfiguration {

    /**
     * Creates the MinIO client used by file workflows.
     *
     * @param properties MinIO connection properties
     * @return configured MinIO client
     */
    @Bean
    public MinioClient minioClient(MinioStorageProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.endpoint())
                .credentials(properties.accessKey(), properties.secretKey())
                .build();
    }
}
