package ru.atnagullova.cloud_storage.exception_handler;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Error response data DTO")
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponseData {

    @Schema(name = "message", example = "Text of error")
    private String message;

}
