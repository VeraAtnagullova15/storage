package ru.atnagullova.cloud_storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.configuration.security.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.service.StorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;


    @PostMapping
    public ResponseEntity<List<ResourceInfoDto>> upload(@RequestParam String path,
                                                        @RequestParam List<MultipartFile> files,
                                                        Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
        Long userId = userDetails.getId();

        List<ResourceInfoDto> uploadedList = storageService.upload(userId, path, files);

        return new ResponseEntity<>(uploadedList, HttpStatus.CREATED);

    }
}
