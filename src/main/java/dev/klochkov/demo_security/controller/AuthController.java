package dev.klochkov.demo_security.controller;

import dev.klochkov.demo_security.dto.security.JwtAuthenticationResponse;
import dev.klochkov.demo_security.dto.security.SignInDto;
import dev.klochkov.demo_security.dto.security.SignUpDto;
import dev.klochkov.demo_security.dto.security.TokenRefreshDto;
import dev.klochkov.demo_security.entity.RefreshToken;
import dev.klochkov.demo_security.exception.GlobalExceptionHandler;
import dev.klochkov.demo_security.exception.TokenRefreshException;
import dev.klochkov.demo_security.service.AuthenticationService;
import dev.klochkov.demo_security.service.JwtService;
import dev.klochkov.demo_security.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "BadRequest", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"messages\":\"неверные параметры\"}"))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "BadRequest", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"messages\":\"internal server exception\"}"))
            })
    })
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpDto request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "BadRequest", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"messages\":\"неверные параметры\"}"))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "BadRequest", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"messages\":\"internal server exception\"}"))
            })
    })
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInDto request) {
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Обновление токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "BadRequest", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"messages\":\"неверные параметры\"}"))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "BadRequest", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"messages\":\"internal server exception\"}"))
            })
    })
    @PostMapping("/refresh-token")
    public JwtAuthenticationResponse refreshToken(@RequestBody @Valid TokenRefreshDto request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return JwtAuthenticationResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken)
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    @Operation(summary = "выход")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "BadRequest", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"error\":\"Bad Request\",\"messages\":\"неверные параметры\"}"))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "BadRequest", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"error\":\"Internal Server Error\",\"messages\":\"internal server exception\"}"))
            })
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestBody @Valid TokenRefreshDto request) {
        String userName = jwtService.extractUserName(request.getRefreshToken());
        refreshTokenService.removeByUser(userName);
    }
}
