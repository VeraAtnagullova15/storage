package ru.atnagullova.cloud_storage.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.configuration.minio.MinioProperties;
import ru.atnagullova.cloud_storage.dto.DownloadFileInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceType;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StorageServiceImpl implements StorageService {

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

        List<ResourceInfoDto> uploadedFiles = new ArrayList<>();
        String userFolderKey = createUserFolderKey(userId, path);

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

                uploadedFiles.add(new ResourceInfoDto(userFolderKey, fileName, file.getSize(), ResourceType.FILE));
            }
        } catch (Exception e) {
            //TODO
        }

        return uploadedFiles;
    }

    @Override
    public List<ResourceInfoDto> getDirectoryInfo(Long userId, String path) {
        return List.of();
    }

    @Override
    public ResourceInfoDto createEmptyDirectory(Long userId, String path) {
        return null;
    }

    private String createUserFolderKey(Long userId, String path) {
        return "user-" + userId + "-files/" + path;
    }
}


