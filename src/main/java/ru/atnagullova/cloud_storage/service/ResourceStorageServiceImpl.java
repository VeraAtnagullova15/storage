package ru.atnagullova.cloud_storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.dto.DownloadedFileInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceType;
import ru.atnagullova.cloud_storage.exception.*;
import ru.atnagullova.cloud_storage.repository.MinioRepository;
import ru.atnagullova.cloud_storage.util.PathBuilderUtil;
import ru.atnagullova.cloud_storage.util.PathValidationUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceStorageServiceImpl implements ResourceStorageService {

    private final MinioRepository minioRepository;

    @Override
    public ResourceInfoDto getInfo(Long userId, String path) {

        if (!PathValidationUtil.isValidPath(path)) {
            throw new InvalidPathException("Incorrect path");
        }

        String userObjectKey = PathBuilderUtil.buildObjectKey(userId, path);

        if (PathValidationUtil.isPathDirectoryCheck(userId, path)) {

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
        long size = minioRepository.getObjectSize(userObjectKey, path);

            return new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, userObjectKey),
                    PathBuilderUtil.getObjectName(userObjectKey), size, ResourceType.FILE);
    }

    @Override
    public void delete(Long userId, String path) {

        if (!PathValidationUtil.isValidPath(path)) {
            throw new InvalidPathException("Incorrect path");
        }

        String userObjectKey = PathBuilderUtil.buildObjectKey(userId, path);

        if ((!minioRepository.getObjectInfo(userObjectKey) &&
                (!PathValidationUtil.isPathDirectoryCheck(userId, path))) ||
                (PathValidationUtil.isPathDirectoryCheck(userId, path) &&
                        (!minioRepository.isDirectoryExists(userObjectKey)))) {
            throw new ResourceNotFoundException("Resource is not found");
        }

        try {
            if (PathValidationUtil.isPathDirectoryCheck(userId, path)) {

                minioRepository.removeDirectory(userObjectKey);

            } else {

                minioRepository.getObjectInfo(userObjectKey, path);
                minioRepository.removeObject(userObjectKey);
            }
        } catch (Exception e) {
            log.error("Unexpected error while delete object", e);
            throw new StorageUnexpectedException("Delete was failed");
        }
    }

    @Override
    public DownloadedFileInfoDto download(Long userId, String path) {

        if (!PathValidationUtil.isValidPath(path)) {
            throw new InvalidPathException("Incorrect path");
        }

        String userObjectKey = PathBuilderUtil.buildObjectKey(userId, path);

        if (PathValidationUtil.isPathDirectoryCheck(userId, path)) {

            File temporaryFile;
            try {
                temporaryFile = File.createTempFile("download", ".zip");
            } catch (IOException e) {
                log.error("Create temporary file was failed", e);
                throw new StorageMinioException("Prepare to download was failed");
            }

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(temporaryFile))) {

                List<String> objectNames = minioRepository.getDirectoryObjectNames(userObjectKey);

                for (String name : objectNames) {
                    if (name.equals(userObjectKey)) {
                        continue;
                    }
                    String rightName = name.substring(userObjectKey.length());

                    if (name.endsWith("/")) {
                        ZipEntry zipEntryDir = new ZipEntry(rightName);
                        zipOutputStream.putNextEntry(zipEntryDir);
                        zipOutputStream.closeEntry();
                    }

                    ZipEntry zipEntry = new ZipEntry(rightName);
                    zipOutputStream.putNextEntry(zipEntry);
                    try (InputStream inputStream = minioRepository.downloadFile(name, path)) {

                        inputStream.transferTo(zipOutputStream);
                    }
                    zipOutputStream.closeEntry();
                }

                InputStream stream = Files.newInputStream(temporaryFile.toPath(), StandardOpenOption.DELETE_ON_CLOSE);

                return new DownloadedFileInfoDto(stream, PathBuilderUtil.getObjectName(userObjectKey) + ".zip",
                        temporaryFile.length());

            } catch (Exception e) {
                temporaryFile.delete();
                log.error("Unexpected error while download directory", e);
                throw new StorageMinioException("Download directory was failed");
            }
        }

        long size = minioRepository.getObjectSize(userObjectKey, path);
        InputStream inputStream = minioRepository.downloadFile(userObjectKey, path);

        DownloadedFileInfoDto downloadedFileInfo = new DownloadedFileInfoDto(inputStream,
                PathBuilderUtil.getObjectName(userObjectKey), size);

        return downloadedFileInfo;
    }

    @Override
    public ResourceInfoDto renameOrMove(Long userId, String from, String to) {

        if (!PathValidationUtil.isValidPath(from) || !PathValidationUtil.isValidPath(to)) {
            throw new InvalidPathException("Incorrect path");
        }

        String fromKey = PathBuilderUtil.buildObjectKey(userId, from);
        String toKey = PathBuilderUtil.buildObjectKey(userId, to);

        if (PathValidationUtil.isPathDirectoryCheck(userId, from)) {

            if (!minioRepository.isDirectoryExists(fromKey)) {
                throw new ResourceNotFoundException("Directory not found " + PathBuilderUtil.getObjectName(fromKey));
            }
            if (minioRepository.isDirectoryExists(toKey)) {
                throw new ResourceAlreadyExistsException("Directory already exists " + PathBuilderUtil.getObjectName(toKey));
            }
            if (fromKey.equals(toKey)) {
                throw new InvalidPathException("Directory couldn't be moved to itself");
            }

            List<String> copiedKeys = new ArrayList<>();
            try {
            List<String> sourceKeys = minioRepository.getDirectoryObjectNames(fromKey);

            for (String sourceKey : sourceKeys) {
                String objectName = sourceKey.substring(fromKey.length());
                String newKey = toKey + objectName;

                minioRepository.copyObject(sourceKey, newKey);
                copiedKeys.add(newKey);
            }
            delete(userId, from);

        } catch (Exception e) {
                minioRepository.rollback(copiedKeys);
                log.error("Error while rename/move directory " + fromKey, e);
                throw new StorageUnexpectedException("Unexpected error while rename/move directory " + from);
            }

            return new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, toKey), PathBuilderUtil.getObjectName(toKey),
                    null, ResourceType.DIRECTORY);
        }

        if (!minioRepository.getObjectInfo(fromKey)) {
            throw new ResourceNotFoundException("Resource not found " + PathBuilderUtil.getObjectName(fromKey));
        }
        if (minioRepository.getObjectInfo(toKey)) {
            throw new ResourceAlreadyExistsException("Resource already exists " + PathBuilderUtil.getObjectName(toKey));
        }

        minioRepository.renameOrMoveObject(fromKey, toKey);
        long size = minioRepository.getObjectSize(toKey, to);

        return new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, toKey),
                PathBuilderUtil.getObjectName(toKey), size, ResourceType.FILE);
    }

    @Override
    public List<ResourceInfoDto> search(Long userId, String query) {

        if (!PathValidationUtil.isValidPath(query)) {
            throw new InvalidPathException("Invalid query");
        }

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, "");
        List<ResourceInfoDto> searchResults = new ArrayList<>();
        Set<String> visitedFolders = new HashSet<>();
        try {
            List<String> objectNames = minioRepository.getDirectoryObjectNames(userFolderKey);

            for (String name : objectNames) {
                if (name.equals(userFolderKey)) {
                    continue;
                }

                for (int j = name.indexOf('/', userFolderKey.length());
                     j != -1;
                     j = name.indexOf('/', j + 1)) {

                    String folderKey = name.substring(0, j + 1);
                    String folderName = PathBuilderUtil.getObjectName(folderKey);
                    if (folderName.contains(query) && visitedFolders.add(folderKey)) {
                        searchResults.add(new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, folderKey),
                                PathBuilderUtil.getObjectName(folderName), null, ResourceType.DIRECTORY));
                    }
                }
                long size = minioRepository.getObjectSize(name, query);

                if (PathBuilderUtil.getObjectName(name).contains(query)) {
                    searchResults.add(new ResourceInfoDto(PathBuilderUtil.getParentFolderPath(userId, name),
                            PathBuilderUtil.getObjectName(name), size, ResourceType.FILE));
                }
            }
        } catch (Exception e) {
            log.error("Error while searching files", e);
            throw new StorageMinioException("Unexpected error while search files " + query);
        }

        return searchResults;
    }

    @Override
    public List<ResourceInfoDto> upload(Long userId, String path, List<MultipartFile> object) {

        if (!PathValidationUtil.isValidPath(path)) {
            throw new InvalidPathException("Incorrect path");
        }

        String userFolderKey = PathBuilderUtil.buildObjectKey(userId, path);

        for (MultipartFile file : object) {
            String originalName = file.getOriginalFilename();
            String objectKey = userFolderKey + originalName;

            if (originalName == null) {
                throw new ResourceNotFoundException("Resource is not found");
            }

            if (minioRepository.getObjectInfo(objectKey)) {
                throw new ResourceAlreadyExistsException("Resource already exists " + file.getOriginalFilename());
            }

            String topFolderKey = PathBuilderUtil.getTopFolder(userFolderKey, originalName);
            if (minioRepository.isDirectoryExists(topFolderKey)) {
                throw new ResourceAlreadyExistsException("Directory already exists");
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


