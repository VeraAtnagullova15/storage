package ru.atnagullova.cloud_storage.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.configuration.minio.MinioProperties;
import ru.atnagullova.cloud_storage.dto.DownloadFileInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceType;
import ru.atnagullova.cloud_storage.exception.ResourceAlreadyExistsException;
import ru.atnagullova.cloud_storage.exception.StorageMinioException;
import ru.atnagullova.cloud_storage.util.PathBuilderUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceStorageServiceImpl implements ResourceStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public ResourceInfoDto getInfo(Long userId, String path) {
        return null;
    }

    @Override
    public void delete(Long userId, String path) {
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
                }
                log.error("Error with checking file {}", objectKey, errorResponseException);
                throw new StorageMinioException("MinIO error while checking existing file " + objectKey);
            } catch (Exception e) {
                log.error("Unexpected error with cheking existing file {}", objectKey, e);
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


