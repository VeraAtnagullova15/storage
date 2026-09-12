package ru.atnagullova.cloud_storage.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.atnagullova.cloud_storage.configuration.minio.MinioProperties;
import ru.atnagullova.cloud_storage.dto.DirectoryInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceType;
import ru.atnagullova.cloud_storage.exception.NoSuchDirectoryException;
import ru.atnagullova.cloud_storage.exception.StorageMinioException;
import ru.atnagullova.cloud_storage.util.PathBuilderUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryStorageServiceImpl implements DirectoryStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public List<ResourceInfoDto> getDirectoryInfo(Long userId, String path) {

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);
        List<ResourceInfoDto> directoryResults = new ArrayList<>();

        try {
            Iterable<Result<Item>> itemsCheckingExist = minioClient.listObjects(ListObjectsArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .prefix(userFolderKey)
                            .maxKeys(1)
                    .build());

            if (!itemsCheckingExist.iterator().hasNext()) {
                throw new NoSuchDirectoryException("Directory not found " + path);
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

                directoryResults.add(new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, name),
                        PathBuilderUtil.getObjectName(name),
                        isDir ? null : item.size(),
                        isDir ? ResourceType.DIRECTORY : ResourceType.FILE));
            }

            return directoryResults;

        } catch (Exception e) {
            throw new StorageMinioException("Getting directory resources failed");
        }
    }

    @Override
    public DirectoryInfoDto createEmptyDirectory(Long userId, String path) {
        return null;
    }
}
