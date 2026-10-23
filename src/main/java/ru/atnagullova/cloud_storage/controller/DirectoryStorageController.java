package ru.atnagullova.cloud_storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.atnagullova.cloud_storage.configuration.security.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.DirectoryInfoDto;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.exception_handler.ErrorResponseData;

import java.util.List;


@Tag(name = "Cloud storage module")
@RequestMapping("/api/directory")
public interface DirectoryStorageController {


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Directory info is got", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ResourceInfoDto.class
                    )
            )),
            @ApiResponse(responseCode = "400", description = "Invalid path", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Directory is not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "500", description = "Unknown error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            ))
    })
    @Operation(summary = "Get info about directory on the storage",
            description = "The response includes path, name, size and type of the resource")
    @GetMapping
    ResponseEntity<List<ResourceInfoDto>> getDirectoryInfo(@RequestParam String path,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails);



    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Directory is created", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = DirectoryInfoDto.class
                    )
            )),
            @ApiResponse(responseCode = "400", description = "Invalid path", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Parent directory is not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "409", description = "Directory already exists", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "500", description = "Unknown error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            ))
    })
    @Operation(summary = "Create empty directory on the storage",
            description = "Create new empty directory. The response includes path, name, size and type of the resource.")
    @PostMapping
    ResponseEntity<DirectoryInfoDto> createEmptyDirectory(@RequestParam String path,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails);
}
