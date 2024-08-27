package dev.klochkov.demo_security.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление токена")
public class TokenRefreshDto {
    @Schema(description = "токен для обновления", example = "eyJhbGciOiJIUzI1NiJ9."
            + "eyJzdWIiOiJWbGFkaW1pciIsImlhdCI6MTcyNDY4NDE0NiwiZXhwIjoxNzI1Mjg4OTQ2fQ."
            + "-g1Xq5QUCzB-q68_4FCPoLpvNGoglDJeskvsNi3SXPA")
    @NotBlank
    private String refreshToken;
}
