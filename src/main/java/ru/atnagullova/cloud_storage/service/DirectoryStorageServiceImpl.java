package ru.atnagullova.cloud_storage.service;

import org.springframework.stereotype.Service;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;

import java.util.List;

@Service
public class DirectoryStorageServiceImpl implements DirectoryStorageService {


    @Override
    public List<ResourceInfoDto> getDirectoryInfo(Long userId, String path) {
        return List.of();
    }

    @Override
    public ResourceInfoDto createEmptyDirectory(Long userId, String path) {
        return null;
    }
}
