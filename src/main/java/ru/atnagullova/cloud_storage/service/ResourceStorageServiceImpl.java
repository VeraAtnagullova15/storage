package ru.atnagullova.cloud_storage.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
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
    public List<ResourceInfoDto> upload(Long userId, String path, List<MultipartFile> files) {

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);

        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String objectKey = userFolderKey + fileName;

                minioClient.statObject(StatObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectKey)
                        .build());
                throw new ResourceAlreadyExistsException("Resource already exists " + file.getOriginalFilename());
            }
        } catch (ResourceAlreadyExistsException resourceAlreadyExistsException) {
            resourceAlreadyExistsException.getMessage();
        } catch (ErrorResponseException errorResponseException) {
            if (!"NoSuchKey".equals(errorResponseException.errorResponse().code())) {
                throw new StorageMinioException("MinIO error");
            }
        } catch (Exception e) {
            throw new StorageMinioException("Error with checking file");
        }

        List<ResourceInfoDto> uploadedFiles = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
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
            throw new StorageMinioException("Upload was failed");
        }

        return uploadedFiles;
    }
    
}


