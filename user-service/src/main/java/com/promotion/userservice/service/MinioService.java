package com.promotion.userservice.service;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

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

    /**
     * Upload or replace a single user profile image.
     * @param file The profile image.
     * @param userId The ID of the user (used to name the file).
     * @return Public URL of the uploaded image.
     */
    public String uploadOrReplaceProfileImage(MultipartFile file, String userId) {
        try {
            // Ensure bucket exists
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            // Construct filename as {userId}.extension (e.g. "1234.png")
            String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("profile.png");
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String filename = userId + "." + extension;

            // Delete old file if it exists
            try {
                minioClient.statObject(
                        StatObjectArgs.builder().bucket(bucket).object(filename).build()
                );
                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucket).object(filename).build()
                );
            } catch (ErrorResponseException e) {
                // Ignore if not found (No existing profile image)
            }

            // Upload the new file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioUrl + "/" + bucket + "/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload or replace profile image: " + e.getMessage(), e);
        }
    }

    /**
     * Get the profile image URL for a user.
     */
    public String getProfileImageUrl(String userId, String extension) {
        return minioUrl + "/" + bucket + "/" + userId + "." + extension;
    }

    /**
     * Delete profile image for a user.
     */
    public void deleteProfileImage(String userId, String extension) {
        try {
            String filename = userId + "." + extension;
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(filename).build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete profile image: " + e.getMessage());
        }
    }
}
