package ru.atnagullova.cloud_storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.atnagullova.cloud_storage.dto.SignUpAndInRequestDto;
import ru.atnagullova.cloud_storage.dto.UserResponseDto;
import ru.atnagullova.cloud_storage.exception_handler.ErrorResponseData;

@Tag(name = "Authentication module")
@RequestMapping("/api/auth")
public interface AuthenticationController {



    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "New user was created", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = UserResponseDto.class
                    )
            )),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )
            )),
            @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    ))),
            @ApiResponse(responseCode = "500", description = "Unknown error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )))
    })
    @Operation(summary = "Sign-up new user",
            description = "Create new user and sign-in them automatically. The response includes username")
    @PostMapping("/sign-up")
    ResponseEntity<UserResponseDto> signUp(
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                   description = "Username and password",
                                                   required = true
                                           )
                                           @Valid @RequestBody SignUpAndInRequestDto requestDto);



    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "New user was created", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = UserResponseDto.class
                    )
            )),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    ))),
            @ApiResponse(responseCode = "401", description = "Wrong data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    ))),
            @ApiResponse(responseCode = "500", description = "Unknown error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseData.class
                    )))
    })
    @Operation(summary = "Sign-in existing user",
            description = "Sign-in existing user. The response includes username")
    @PostMapping("/sign-in")
    ResponseEntity<UserResponseDto> signIn(
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                   description = "Username and password",
                                                   required = true
                                           )
                                           @Valid @RequestBody SignUpAndInRequestDto requestDto);
}
