package ru.atnagullova.cloud_storage.repository;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.atnagullova.cloud_storage.configuration.minio.MinioProperties;
import ru.atnagullova.cloud_storage.dto.StorageObjectInfoDto;
import ru.atnagullova.cloud_storage.exception.ResourceNotFoundException;
import ru.atnagullova.cloud_storage.exception.StorageMinioException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MinioRepository {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;


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

    public long getObjectSize(String objectKey, String path) {

        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .build());
            return statObjectResponse.size();

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

    private Iterable<Result<Item>> getDirectoryInfo(String folderKey, boolean recursive) {

        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(minioProperties.getBucket())
                .prefix(folderKey)
                .recursive(recursive)
                .build());
    }

    public List<String> getDirectoryObjectNames(String folderKey) {

        List<String> names = new ArrayList<>();
        try {
            Iterable<Result<Item>> items = getDirectoryInfo(folderKey, true);
            for (Result<Item> i : items) {
                Item item = i.get();
                String name = item.objectName();
                names.add(name);
            }
        } catch (Exception e) {
            log.error("Error while getting names of objects from directory " + folderKey);
            throw new StorageMinioException("Unexpected error while getting directory info " + folderKey);
        }

        return names;
    }

    public List<StorageObjectInfoDto> getStorageObjectsInfo(String folderKey) {

        List<StorageObjectInfoDto> storageObjects = new ArrayList<>();
        try {
            Iterable<Result<Item>> items = getDirectoryInfo(folderKey, false);
            for (Result<Item> i : items) {
                Item item = i.get();
                String name = item.objectName();
                long size = item.size();
                boolean isDir = item.isDir();
                storageObjects.add(new StorageObjectInfoDto(name, size, isDir));
            }
        } catch (Exception e) {
            log.error("Error while getting names of objects from directory " + folderKey);
            throw new StorageMinioException("Unexpected error while getting directory info " + folderKey);
        }

        return storageObjects;
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

    public void removeDirectory(String folderKey) {

        try {
            List<DeleteObject> toDelete = new ArrayList<>();
            Iterable<Result<Item>> items = getDirectoryInfo(folderKey, true);

            for (Result<Item> result : items) {
                toDelete.add(new DeleteObject(result.get().objectName()));
            }
            if (toDelete.isEmpty()) {
                throw new ResourceNotFoundException("Directory not found");
            }
            Iterable<Result<DeleteError>> errors = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .objects(toDelete)
                    .build());
            for (Result<DeleteError> resultError : errors) {
                resultError.get();

            }
        } catch (Exception e) {
            log.error("Unexpected error while delete object", e);
            throw new StorageMinioException("Delete was failed");
        }
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

    public void copyObject(String from, String to) {

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(to)
                    .source(CopySource.builder()
                            .bucket(minioProperties.getBucket())
                            .object(from)
                            .build())
                    .build());
        } catch (Exception e) {
            log.error("Errror while copy object");
            throw new StorageMinioException("Error while rename or move object");
        }
    }

    public InputStream downloadFile(String objectKey, String path) {

        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .build());
        } catch (ErrorResponseException errorResponseException) {
            if ("NoSuchKey".equals(errorResponseException.errorResponse().code())) {
                throw new ResourceNotFoundException("Resource not found " + path);
            }
            log.error("Minio error while download file {}", objectKey, errorResponseException);
            throw new StorageMinioException("Error while download file " + path);
        } catch (Exception e) {
            throw new StorageMinioException("Unexpected error while download file");
        }
    }

    public void renameOrMoveObject(String fromKey, String toKey) {

        copyObject(fromKey, toKey);
        removeObject(fromKey);

    }

    public void rollback (List<String> copiedObjectNames) {
        try {
            for (String name : copiedObjectNames) {
                removeObject(name);
            }
        } catch (Exception e) {
            log.error("Rollback failed");
        }
    }
}
