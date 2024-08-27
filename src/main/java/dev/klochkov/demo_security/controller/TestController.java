package dev.klochkov.demo_security.controller;

import dev.klochkov.demo_security.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    @Operation(summary = "Получение приветствия для пользователя с ролью USER")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Hello Vladimir. You have next authorities [USER]. "
                                    + "This endpoint for  user"))
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
    @GetMapping("user")
    public ResponseEntity<String> greetingUserWithRole() {
        String response = buildStringForGreeting() +" user";
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение приветствия для пользователя с ролью ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Hello Vladimir. You have next authorities [ADMIN]. "
                                    + "This endpoint for  admin"))
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
    @GetMapping("/admin")
    public ResponseEntity<String> greetingAdmin() {
        String response = buildStringForGreeting() +" admin";
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение приветствия для пользователя с ролью SUPER_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Hello Vladimir. You have next authorities [SUPER_ADMIN]. "
                                    + "This endpoint for  super_admin"))
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
    @GetMapping("/super-admin")
    public ResponseEntity<String> greetingSuperAdmin() {
        String response = buildStringForGreeting() +" super_admin";
        return ResponseEntity.ok(response);
    }

    private String buildStringForGreeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        String authorities = authentication.getAuthorities().toString();
        return "Hello %s. You have next authorities %s. This endpoint for".formatted(name, authorities);
    }
}
