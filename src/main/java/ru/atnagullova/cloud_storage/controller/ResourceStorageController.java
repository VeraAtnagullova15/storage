package ru.atnagullova.cloud_storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.atnagullova.cloud_storage.configuration.security.UserDetailsImpl;
import ru.atnagullova.cloud_storage.dto.ResourceInfoDto;
import ru.atnagullova.cloud_storage.exception_handler.ErrorResponseData;

import java.util.List;

@Tag(name = "Cloud storage module")
@RequestMapping("/api/resource")
public interface ResourceStorageController {


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource info is got", content = @Content(
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
            @ApiResponse(responseCode = "404", description = "Resource is not found", content = @Content(
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
    @Operation(summary = "Get info about resource on the storage",
            description = "The response includes path, name, size and type of the resource")
    @GetMapping
    ResponseEntity<ResourceInfoDto> getInfo(@RequestParam String path,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails);



    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource/directory successfully is upload", content = @Content(
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
            @ApiResponse(responseCode = "500", description = "Unknown error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            ))
    })
    @Operation(summary = "Upload file or directory on the storage",
            description = "The response includes List of path, name, size and type of the resource")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<List<ResourceInfoDto>> upload(@RequestParam String path,
                                                 @RequestParam List<MultipartFile> object,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails);



    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resource/directory is deleted"),
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
            @ApiResponse(responseCode = "404", description = "Resource/directory is not found", content = @Content(
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
    @Operation(summary = "Delete file or directory from the storage",
            description = "The response is void")
    @DeleteMapping
    ResponseEntity<Void> delete(@RequestParam String path,
                                @AuthenticationPrincipal UserDetailsImpl userDetails);



    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource/directory is renamed/moved", content = @Content(
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
            @ApiResponse(responseCode = "404", description = "Resource is not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "409", description = "Resource already exists", content = @Content(
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
    @Operation(summary = "Rename or move file or directory on tha storage",
            description = "The response includes List of path, name, size and type of the resource")
    @PostMapping("/move")
    ResponseEntity<ResourceInfoDto> renameOrMove(@RequestParam String from,
                                                 @RequestParam String to,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails);



    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource/directory is searched by query", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ResourceInfoDto.class
                    )
            )),
            @ApiResponse(responseCode = "400", description = "Invalid query", content = @Content(
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
            @ApiResponse(responseCode = "500", description = "Unknown error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            ))
    })
    @Operation(summary = "Search file or directory by name on the storage",
            description = "The response includes List of path, name, size and type of the resource")
    @GetMapping("/search")
    ResponseEntity<List<ResourceInfoDto>> search (@RequestParam String query,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails);



    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource/directory is download", content = @Content(
                    mediaType = "application/octet-stream")
            ),
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
            @ApiResponse(responseCode = "404", description = "Resource/directory is not found", content = @Content(
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
    @Operation(summary = "Download file or directory from the storage",
            description = "Directories download as a zip-archive. " +
                    "The response includes path, name, size and type of the resource")
    @GetMapping(value = "/download")
    ResponseEntity<InputStreamResource> download (@RequestParam String path,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails);

}