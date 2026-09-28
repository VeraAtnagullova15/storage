package ru.atnagullova.cloud_storage.repository;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.atnagullova.cloud_storage.configuration.minio.MinioProperties;
import ru.atnagullova.cloud_storage.exception.ResourceNotFoundException;
import ru.atnagullova.cloud_storage.exception.StorageMinioException;

import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MinioRepository {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;


    public StatObjectResponse isObjectExists(String objectKey, Long userId, String path) {

        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .build());
            return statObjectResponse;

        } catch (ErrorResponseException errorResponseException) {
            if ("NoSuchKey".equals(errorResponseException.errorResponse().code())) {
                throw new ResourceNotFoundException("Resource not found " + path);
            }
            log.error("Minio error while getting file info {}", objectKey, errorResponseException);
            throw new StorageMinioException("Error while getting file info " + path);
        } catch (Exception e) {
            log.error("Unexpected error  while getting file info {}", objectKey, e);
            throw new StorageMinioException("Getting file info " + path + "was failed");
        }
    }

    public boolean isObjectExists(String objectKey) {

        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .build());
        } catch (ErrorResponseException errorResponseException) {
            if ("NoSuchKey".equals(errorResponseException.errorResponse().code())) {
                return false;
            } else {
                log.error("Error with checking file {}", objectKey, errorResponseException);
                throw new StorageMinioException("MinIO error while checking existing file " + objectKey);
            }
        } catch (Exception e) {
            log.error("Unexpected error while checking existing file {}", objectKey, e);
            throw new StorageMinioException("Error with checking file");
        }
        return true;
    }


    public Iterable<Result<Item>> getDirectoryInfo(String folderKey, boolean recursive) {

        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(minioProperties.getBucket())
                .prefix(folderKey)
                .recursive(recursive)
                .build());
    }


    public boolean isDirectoryExists(String folderKey) {

        return minioClient.listObjects(ListObjectsArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .prefix(folderKey)
                        .maxKeys(1)
                        .build())
                .iterator().hasNext();
    }




    public void putObject(String objectKey, String contentType, InputStream stream, long objectSize, long partSize) {

        if (contentType == null) {
            try {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectKey)
                        .stream(stream, objectSize, partSize)
                        .build());
            } catch (Exception e) {
                log.error("Creating directory failed", e);
                throw new StorageMinioException("Creating directory failed");
            }
        } else {
            try {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectKey)
                        .contentType(contentType)
                        .stream(stream, objectSize, partSize)
                        .build());
            } catch (Exception e) {
                log.error("Upload failed", e);
                throw new StorageMinioException("Upload was failed");
            }
        }
    }

    public Iterable<Result<DeleteError>> removeDirectory(List<DeleteObject> toDelete) {

        return minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(minioProperties.getBucket())
                .objects(toDelete)
                .build());
    }

    public void removeObject(String objectKey) {

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .build());
        } catch (ErrorResponseException errorResponseException) {
            if ("NoSuchKey".equals(errorResponseException.errorResponse().code())) {
                throw new ResourceNotFoundException("Resource not found " + objectKey);
            }
            log.error("Minio error while delete resource {}", objectKey, errorResponseException);
            throw new StorageMinioException("Delete resource error " + objectKey);
        } catch (Exception e) {
            log.error("Unexpected error while delete object");
            throw new StorageMinioException("Delete resource error " + objectKey);
        }
    }
}
