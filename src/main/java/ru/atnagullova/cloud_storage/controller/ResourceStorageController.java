package ru.atnagullova.cloud_storage.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.configuration.security.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;

import java.util.List;

@RequestMapping("/api/resource")
public interface ResourceStorageController {

    @GetMapping
    ResponseEntity<ResourceInfoDto> getInfo(@RequestParam String path,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<List<ResourceInfoDto>> upload(@RequestParam String path,
                                                 @RequestParam List<MultipartFile> object,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails);

}
