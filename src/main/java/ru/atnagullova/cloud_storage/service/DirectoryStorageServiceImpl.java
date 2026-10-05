package ru.atnagullova.cloud_storage.service;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.atnagullova.cloud_storage.dto.DirectoryInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceType;
import ru.atnagullova.cloud_storage.exception.InvalidPathException;
import ru.atnagullova.cloud_storage.exception.ResourceNotFoundException;
import ru.atnagullova.cloud_storage.exception.ResourceAlreadyExistsException;
import ru.atnagullova.cloud_storage.exception.StorageMinioException;
import ru.atnagullova.cloud_storage.repository.MinioRepository;
import ru.atnagullova.cloud_storage.util.PathBuilderUtil;
import ru.atnagullova.cloud_storage.util.PathValidationUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryStorageServiceImpl implements DirectoryStorageService {

    private final MinioRepository minioRepository;

    @Override
    public List<ResourceInfoDto> getDirectoryInfo(Long userId, String path) {

        if (!PathValidationUtils.isValidPath(path)) {
            throw new InvalidPathException("Invalid path");
        }

        if (!PathValidationUtils.isPathDirectoryCheck(userId, path)) {
            throw new InvalidPathException("Directory path must end with slash");
        }

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);
        List<ResourceInfoDto> directoryResults = new ArrayList<>();

        try {
            if (!minioRepository.isDirectoryExists(userFolderKey)) {
                throw new ResourceNotFoundException("Directory not found " + path);
            }

            Iterable<Result<Item>> results = minioRepository.getDirectoryInfo(userFolderKey, false);

            for (Result<Item> i : results) {
                Item item = i.get();
                String name = item.objectName();
                boolean isDir = item.isDir();

                if (name.equals(userFolderKey) && item.size() == 0 || name.endsWith(".DS_Store")) {
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
            log.error("Unexpected error while getting directory info {}", userFolderKey, e);
            throw new StorageMinioException("Getting directory resources failed");
        }

        return directoryResults;
    }

    @Override
    public DirectoryInfoDto createEmptyDirectory(Long userId, String path) {

        if (!PathValidationUtils.isValidPath(path)) {
            throw new InvalidPathException("Incorrect path");
        }

        if (!PathValidationUtils.isPathDirectoryCheck(userId,path)) {
            throw new InvalidPathException("Directory path must end with slash");
        }

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);

        try {
            if (minioRepository.isDirectoryExists(userFolderKey)) {
                throw new ResourceAlreadyExistsException("Directory already exists " + path);
            }
            if (!isParentDirectoryExists(userId, userFolderKey)) {
                throw new ResourceNotFoundException("Parent directory not found");
            }

            minioRepository.putObject(userFolderKey, null, new ByteArrayInputStream(new byte[0]), 0, -1);

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


    private boolean isParentDirectoryExists(Long userId, String folderKey) {

        String parentPath = PathBuilderUtil.getParentFolderPath(userId, folderKey);
        if (parentPath.isEmpty()) {
            return true;
        }
        String parentKey = PathBuilderUtil.buildObjectKey(userId, parentPath);

        return minioRepository.isDirectoryExists(parentKey);
    }


}
