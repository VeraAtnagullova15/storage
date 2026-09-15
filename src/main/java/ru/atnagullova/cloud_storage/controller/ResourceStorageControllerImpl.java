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

    @Override
    public ResponseEntity<ResourceInfoDto> getInfo(String path, UserDetailsImpl userDetails) {

        Long userId = userDetails.getId();
        ResourceInfoDto resourceInfo = storageService.getInfo(userId, path);

        return new ResponseEntity<>(resourceInfo, HttpStatus.OK);
    }

    public ResponseEntity<List<ResourceInfoDto>> upload(String path,
                                                        List<MultipartFile> object,
                                                        UserDetailsImpl userDetails) {

        Long userId = userDetails.getId();

        List<ResourceInfoDto> uploadedList = storageService.upload(userId, path, object);

        return new ResponseEntity<>(uploadedList, HttpStatus.CREATED);

    }

    @Override
    public ResponseEntity<Void> delete(String path, UserDetailsImpl userDetails) {

        Long userId = userDetails.getId();
        storageService.delete(userId, path);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
