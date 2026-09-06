package ru.atnagullova.cloud_storage.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.service.StorageService;

import java.util.List;

public class StorageController {

    @Autowired
    private final StorageService storageService;


    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<List<ResourceInfoDto>> upload(Long userId, String path, MultipartFile files) {


        List<ResourceInfoDto> uploadedList = storageService.upload(userId, path, files);

        return new ResponseEntity<>(uploadedList, HttpStatus.CREATED);

    }
}
