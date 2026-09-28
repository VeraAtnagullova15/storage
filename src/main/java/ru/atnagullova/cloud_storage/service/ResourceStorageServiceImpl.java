package ru.atnagullova.cloud_storage.service;

import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.dto.DownloadedFileInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceType;
import ru.atnagullova.cloud_storage.exception.ResourceAlreadyExistsException;
import ru.atnagullova.cloud_storage.exception.ResourceNotFoundException;
import ru.atnagullova.cloud_storage.exception.StorageUnexpectedException;
import ru.atnagullova.cloud_storage.repository.MinioRepository;
import ru.atnagullova.cloud_storage.util.PathBuilderUtil;
import ru.atnagullova.cloud_storage.util.PathValidationUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceStorageServiceImpl implements ResourceStorageService {

    private final MinioRepository minioRepository;

    @Override
    public ResourceInfoDto getInfo(Long userId, String path) {

        String userObjectKey = PathBuilderUtil.buildObjectKey(userId, path);

        if (PathValidationUtils.isPathDirectoryCheck(userId, path)) {

            try {
                if (!minioRepository.isDirectoryExists(userObjectKey)) {
                    throw new ResourceNotFoundException("Directory is not exist");
                }
            } catch (ResourceNotFoundException resourceNotFoundException) {
                throw resourceNotFoundException;
            } catch (Exception e) {
                log.error("Unexpected error while getting directory info {}", userObjectKey, e);
                throw new StorageUnexpectedException("Getting directory info " + path + "was failed");
            }
            return new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, userObjectKey),
                    PathBuilderUtil.getObjectName(userObjectKey), null, ResourceType.DIRECTORY);
        }

        StatObjectResponse statObjectResponse = minioRepository.isObjectExists(userObjectKey, userId, path);

            return new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, userObjectKey),
                    PathBuilderUtil.getObjectName(userObjectKey), statObjectResponse.size(), ResourceType.FILE);
    }

    @Override
    public void delete(Long userId, String path) {

        String userObjectKey = PathBuilderUtil.buildObjectKey(userId, path);

        try {
            if (PathValidationUtils.isPathDirectoryCheck(userId, path)) {

                List<DeleteObject> toDelete = new ArrayList<>();
                Iterable<Result<Item>> items = minioRepository.getDirectoryInfo(userObjectKey, true);

                for (Result<Item> result : items) {
                    toDelete.add(new DeleteObject(result.get().objectName()));
                }
                if (toDelete.isEmpty()) {
                    throw new ResourceNotFoundException("Directory not found " + path);
                }

                Iterable<Result<DeleteError>> errors = minioRepository.removeDirectory(toDelete);
                for (Result<DeleteError> resultError : errors) {
                    resultError.get();
                }

            } else {

                minioRepository.isObjectExists(userObjectKey, userId, path);

                minioRepository.removeObject(userObjectKey);
            }
        } catch (Exception e) {
            log.error("Unexpected error while delete object", e);
            throw new StorageUnexpectedException("Delete was failed");
        }
    }

    @Override
    public DownloadedFileInfoDto download(Long userId, String path) {
        return null;
    }

    @Override
    public ResourceInfoDto renameOrMove(Long userId, String from, String to) {
        return null;
    }

    @Override
    public List<ResourceInfoDto> search(Long userId, String query) {
        return List.of();
    }

    @Override
    public List<ResourceInfoDto> upload(Long userId, String path, List<MultipartFile> object) {

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);

        for (MultipartFile file : object) {
            String originalName = file.getOriginalFilename();
            String objectKey = userFolderKey + originalName;

            if (minioRepository.isObjectExists(objectKey)) {
                throw new ResourceAlreadyExistsException("Resource already exists " + file.getOriginalFilename());
            }

            int lastSlash = originalName.indexOf('/');
            if (lastSlash > 0) {
                String topFolderKey = PathBuilderUtil.getTopFolder(userFolderKey, originalName, lastSlash);
                if (minioRepository.isDirectoryExists(topFolderKey)) {
                    throw new ResourceAlreadyExistsException("Directory already exists");
                }
            }
        }

        List<ResourceInfoDto> uploadedFiles = new ArrayList<>();
        try {
            for (MultipartFile file : object) {
                String fileName = file.getOriginalFilename();
                String objectKey = userFolderKey + fileName;

                minioRepository.putObject(objectKey, file.getContentType(), file.getInputStream(), file.getSize(), -1);

                uploadedFiles.add(new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, objectKey),
                        PathBuilderUtil.getObjectName(objectKey), file.getSize(), ResourceType.FILE));
            }
        } catch (Exception e) {
            log.error("Upload failed for userId={}, path={}", userId, path, e);
            throw new StorageUnexpectedException("Upload was failed");
        }

        return uploadedFiles;
    }

}


