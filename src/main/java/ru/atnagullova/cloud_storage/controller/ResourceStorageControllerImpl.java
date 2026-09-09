package ru.atnagullova.cloud_storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.configuration.security.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.service.ResourceStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceStorageControllerImpl implements ResourceStorageController {

    private final ResourceStorageService storageService;

    public ResponseEntity<List<ResourceInfoDto>> upload(String path,
                                                        List<MultipartFile> files,
                                                        UserDetailsImpl userDetails) {

        Long userId = userDetails.getId();

        List<ResourceInfoDto> uploadedList = storageService.upload(userId, path, files);

        return new ResponseEntity<>(uploadedList, HttpStatus.CREATED);

    }
}
