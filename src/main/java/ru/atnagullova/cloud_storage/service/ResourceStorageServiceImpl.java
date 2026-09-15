package ru.atnagullova.cloud_storage.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.configuration.minio.MinioProperties;
import ru.atnagullova.cloud_storage.dto.DownloadFileInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceType;
import ru.atnagullova.cloud_storage.exception.ResourceAlreadyExistsException;
import ru.atnagullova.cloud_storage.exception.ResourceNotFoundException;
import ru.atnagullova.cloud_storage.exception.StorageMinioException;
import ru.atnagullova.cloud_storage.util.PathBuilderUtil;
import ru.atnagullova.cloud_storage.util.PathValidationUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceStorageServiceImpl implements ResourceStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final DirectoryStorageService directoryStorageService;

    @Override
    public ResourceInfoDto getInfo(Long userId, String path) {

        String userObjectKey = PathBuilderUtil.buildObjectKey(userId, path);

        if (PathValidationUtils.isPathDirectoryCheck(userId, path)) {

            try {
                if (!directoryStorageService.isDirectoryExists(userObjectKey)) {
                    throw new ResourceNotFoundException("Directory is not exist");
                }
            } catch (ResourceNotFoundException resourceNotFoundException) {
                throw resourceNotFoundException;
            } catch (Exception e) {
                log.error("Unexpected error while getting directory info {}", userObjectKey, e);
                throw new StorageMinioException("Getting directory info " + path + "was failed");
            }
            return new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, userObjectKey),
                    PathBuilderUtil.getObjectName(userObjectKey), null, ResourceType.DIRECTORY);
        }

        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(userObjectKey)
                    .build());

            return new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, userObjectKey),
                    PathBuilderUtil.getObjectName(userObjectKey), statObjectResponse.size(), ResourceType.FILE);
        } catch (ErrorResponseException errorResponseException) {
            if ("NoSuchKey".equals(errorResponseException.errorResponse().code())) {
                throw new ResourceNotFoundException("Resource not found " + path);
            }
            log.error("Minio error while getting file info {}", userObjectKey, errorResponseException);
            throw new StorageMinioException("Error while gettimg file info " + path);
        } catch (Exception e) {
            log.error("Unexpected error  while getting file info {}", userObjectKey, e);
            throw new StorageMinioException("Getting file info " + path + "was failed");
        }
    }

    @Override
    public void delete(Long userId, String path) {
        String userObjectKey = PathBuilderUtil.buildObjectKey(userId, path);

        try {

            if (PathValidationUtils.isPathDirectoryCheck(userId, path)) {
                List<DeleteObject> toDelete = new ArrayList<>();

                Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .prefix(userObjectKey)
                        .recursive(true)
                        .build());

                for (Result<Item> result : items) {
                    toDelete.add(new DeleteObject(result.get().objectName()));
                }

                if (toDelete.isEmpty()) {
                    throw new ResourceNotFoundException("Directory not found " + path);
                }

                Iterable<Result<DeleteError>> errors = minioClient.removeObjects(RemoveObjectsArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .objects(toDelete)
                        .build());

                for (Result<DeleteError> resultError : errors) {
                    resultError.get();
                }
            } else {

                minioClient.statObject(StatObjectArgs.builder()
                                .bucket(minioProperties.getBucket())
                                .object(userObjectKey)
                        .build());

                minioClient.removeObject(RemoveObjectArgs.builder()
                                .bucket(minioProperties.getBucket())
                                .object(userObjectKey)
                        .build());
            }
        } catch (ErrorResponseException errorResponseException) {
            if ("NoSuchKey".equals(errorResponseException.errorResponse().code())) {
                throw new ResourceNotFoundException("Resource not found " + path);
            }
            log.error("Minio error while delete resource {}", userObjectKey, errorResponseException);
            throw new StorageMinioException("Delete resource error " + path);
        } catch (Exception e) {
            log.error("");
            throw new StorageMinioException("Delete resource error " + path);
        }
    }

    @Override
    public DownloadFileInfoDto download(Long userId, String path) {
        return null;
    }

    @Override
    public ResourceInfoDto renameOrRemove(Long userId, String from, String to) {
        return null;
    }

    @Override
    public List<ResourceInfoDto> search(Long userId, String query) {
        return List.of();
    }

    @Override
    public List<ResourceInfoDto> upload(Long userId, String path, List<MultipartFile> object) {

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);

        boolean resourceExists = false;
        for (MultipartFile file : object) {
            String objectKey = userFolderKey + file.getOriginalFilename();

            try {
                minioClient.statObject(StatObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectKey)
                        .build());
                resourceExists = true;
            } catch (ErrorResponseException errorResponseException) {
                if ("NoSuchKey".equals(errorResponseException.errorResponse().code())) {
                    resourceExists = false;
                } else {
                    log.error("Error with checking file {}", objectKey, errorResponseException);
                    throw new StorageMinioException("MinIO error while checking existing file " + objectKey);
                }
            } catch (Exception e) {
                log.error("Unexpected error while cheking existing file {}", objectKey, e);
                throw new StorageMinioException("Error with checking file");
            }
            if (resourceExists) {
                throw new ResourceAlreadyExistsException("Resource already exists " + file.getOriginalFilename());
            }
        }

        List<ResourceInfoDto> uploadedFiles = new ArrayList<>();
        try {
            for (MultipartFile file : object) {
                String fileName = file.getOriginalFilename();
                String objectKey = userFolderKey + fileName;

                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectKey)
                        .contentType(file.getContentType())
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .build());

                uploadedFiles.add(new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, objectKey),
                        PathBuilderUtil.getObjectName(objectKey), file.getSize(), ResourceType.FILE));
            }
        } catch (Exception e) {
            log.error("Upload failed for userId={}, path={}", userId, path, e);
            throw new StorageMinioException("Upload was failed");
        }

        return uploadedFiles;
    }

}


