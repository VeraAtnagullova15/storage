package ru.atnagullova.cloud_storage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.dto.DownloadFileInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;

import java.util.List;

@Service
@Transactional
public class StorageServiceImpl implements StorageService {



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
    public List<ResourceInfoDto> upload(Long userId, String path, MultipartFile file) {
        return List.of();
    }

    @Override
    public List<ResourceInfoDto> getDirectoryInfo(Long userId, String path) {
        return List.of();
    }

    @Override
    public ResourceInfoDto createEmptyDirectory(Long userId, String path) {
        return null;
    }
}
