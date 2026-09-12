package ru.atnagullova.cloud_storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.atnagullova.cloud_storage.configuration.security.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.service.DirectoryStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DirectoryStorageControllerImpl implements DirectoryStorageController {

    private final DirectoryStorageService directoryStorageService;

    @Override
    public ResponseEntity<List<ResourceInfoDto>> getDirectoryInfo(String path, UserDetailsImpl userDetails) {

        Long userId = userDetails.getId();
        List<ResourceInfoDto> directoryResults = directoryStorageService.getDirectoryInfo(userId, path);

        return new ResponseEntity<>(directoryResults, HttpStatus.OK);
    }
}
