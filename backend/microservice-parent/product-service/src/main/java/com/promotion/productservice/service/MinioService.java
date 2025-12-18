package com.promotion.productservice.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<String> uploadMultipleFiles(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();

        if (files == null) return urls;

        if (files.size() > 8) {
            throw new RuntimeException("Maximum of 8 images allowed");
        }

        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            for (MultipartFile file : files) {
                String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(filename)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );

                urls.add(minioUrl + "/" + bucket + "/" + filename);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload images: " + e.getMessage());
        }

        return urls;
    }
}
