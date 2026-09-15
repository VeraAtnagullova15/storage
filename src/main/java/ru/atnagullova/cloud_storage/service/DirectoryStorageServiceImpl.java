package ru.atnagullova.cloud_storage.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.atnagullova.cloud_storage.configuration.minio.MinioProperties;
import ru.atnagullova.cloud_storage.dto.DirectoryInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceType;
import ru.atnagullova.cloud_storage.exception.InvalidPathMinioException;
import ru.atnagullova.cloud_storage.exception.ResourceNotFoundException;
import ru.atnagullova.cloud_storage.exception.ResourceAlreadyExistsException;
import ru.atnagullova.cloud_storage.exception.StorageMinioException;
import ru.atnagullova.cloud_storage.util.PathBuilderUtil;
import ru.atnagullova.cloud_storage.util.PathValidationUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryStorageServiceImpl implements DirectoryStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public List<ResourceInfoDto> getDirectoryInfo(Long userId, String path) {

        if (!PathValidationUtils.isPathDirectoryCheck(userId, path)) {
            throw new InvalidPathMinioException("Directory path must end with slash");
        }

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);
        List<ResourceInfoDto> directoryResults = new ArrayList<>();

        try {
            if (!isDirectoryExists(userFolderKey)) {
                throw new ResourceNotFoundException("Directory not found " + path);
            }

            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .prefix(userFolderKey)
                            .recursive(false)
                    .build());

            for (Result<Item> i : results) {
                Item item = i.get();
                String name = item.objectName();
                boolean isDir = item.isDir();

                if (name.equals(userFolderKey) && item.size() == 0) {
                    continue;
                }

                directoryResults.add(new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, name),
                        PathBuilderUtil.getObjectName(name),
                        isDir ? null : item.size(),
                        isDir ? ResourceType.DIRECTORY : ResourceType.FILE));
            }

        } catch (ResourceNotFoundException directoryException) {
            throw directoryException;
        } catch (Exception e) {
            log.error("Unexpected error while gettimg directory info {}", userFolderKey, e);
            throw new StorageMinioException("Getting directory resources failed");
        }

        return directoryResults;
    }

    @Override
    public DirectoryInfoDto createEmptyDirectory(Long userId, String path) {

        if (!PathValidationUtils.isPathDirectoryCheck(userId,path)) {
            throw new InvalidPathMinioException("Directory path must end with slash");
        }

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);

        try {
            if (isDirectoryExists(userFolderKey)) {
                throw new ResourceAlreadyExistsException("Directory already exists " + path);
            }
            if (!isParentDirectoryExists(userId, userFolderKey)) {
                throw new ResourceNotFoundException("Parent directory not found");
            }

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(userFolderKey)
                    .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                    .build());

        } catch (ResourceAlreadyExistsException alreadyExistsException) {
            throw alreadyExistsException;
        } catch (ResourceNotFoundException resourceNotFoundException) {
            throw resourceNotFoundException;
        } catch (Exception e) {
            log.error("Unexpected error while creating directory {}", userFolderKey, e);
            throw new StorageMinioException("Creation directory was failed " + path);
        }

        return new DirectoryInfoDto(PathBuilderUtil.getParentFolderPath(userId, userFolderKey),
                PathBuilderUtil.getObjectName(userFolderKey), ResourceType.DIRECTORY);
    }


    @Override
    public boolean isDirectoryExists(String folderKey) {

        return minioClient.listObjects(ListObjectsArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .prefix(folderKey)
                        .maxKeys(1)
                        .build())
                .iterator().hasNext();
    }


    private boolean isParentDirectoryExists(Long userId, String folderKey) {

        String parentPath = PathBuilderUtil.getParentFolderPath(userId, folderKey);

        if (parentPath.isEmpty()) {
            return true;
        }

        String parentKey = PathBuilderUtil.buildObjectKey(userId, parentPath);

        return isDirectoryExists(parentKey);
    }


}
