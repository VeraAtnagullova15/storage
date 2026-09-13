package ru.atnagullova.cloud_storage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.atnagullova.cloud_storage.configuration.security.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.DirectoryInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;

import java.util.List;

@RequestMapping("/api/directory")
public interface DirectoryStorageController {

    @GetMapping
    ResponseEntity<List<ResourceInfoDto>> getDirectoryInfo(@RequestParam String path,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails);
    @PostMapping
    ResponseEntity<DirectoryInfoDto> createEmptyDirectory(@RequestParam String path,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails);
}
