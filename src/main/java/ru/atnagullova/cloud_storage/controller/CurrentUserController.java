package ru.atnagullova.cloud_storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.exception_handler.ErrorResponseData;

@Tag(name = "Authentication module")
@RequestMapping("/api/user")
public interface CurrentUserController {


    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Success", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = UserResponseDto.class
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
    @Operation(summary = "Get info about current user",
            description = "The response includes username")
    @GetMapping("/me")
    ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication);
}
