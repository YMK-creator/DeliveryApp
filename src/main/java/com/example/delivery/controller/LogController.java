package com.example.delivery.controller;

import com.example.delivery.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logs")
@Tag(name = "Log Controller", description = "API для работы с логами системы")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @Operation(summary = "Сгенерировать файл логов",
            description = "Запускает асинхронную генерацию файла логов для указанной даты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Задача на генерацию логов принята",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"taskId\": \"12345\", \"status\": \"PROCESSING\", \"statusUrl\": \"/logs/12345/status\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверный формат даты"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/{date}")
    public CompletableFuture<ResponseEntity<Map<String, String>>> generateLogs(
            @Parameter(description = "Дата для генерации логов в формате YYYY-MM-DD",
                    example = "2023-05-15")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        CompletableFuture<String> future = logService.generateLogFileForDateAsync(date.toString());
        return future.thenApply(taskId ->
                ResponseEntity.accepted().body(Map.of(
                        "taskId", taskId,
                        "status", "PROCESSING",
                        "statusUrl", "/logs/" + taskId + "/status"
                ))
        );
    }

    @Operation(summary = "Получить статус задачи",
            description = "Возвращает текущий статус задачи по генерации логов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус задачи получен",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\": \"COMPLETED\"}"))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/{taskId}/status")
    public ResponseEntity<Map<String, String>> getTaskStatus(
            @Parameter(description = "Идентификатор задачи", example = "12345")
            @PathVariable String taskId) {
        String status = logService.getTaskStatus(taskId);
        if ("NOT_FOUND".equals(status)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("status", status));
    }

    @Operation(summary = "Скачать файл логов",
            description = "Загружает сгенерированный файл логов по идентификатору задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл логов успешно загружен",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "425", description = "Файл ещё не готов"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/{taskId}/file")
    public ResponseEntity<Resource> downloadLogFileAsync(
            @Parameter(description = "Идентификатор задачи", example = "12345")
            @PathVariable String taskId) {
        try {
            String status = logService.getTaskStatus(taskId);

            if (!status.startsWith("COMPLETED")) {
                return ResponseEntity.status(status.startsWith("FAILED")
                        ? HttpStatus.NOT_FOUND : HttpStatus.TOO_EARLY).build();
            }

            String filePath = logService.getLogFilePath(taskId);
            if (filePath == null || !Files.exists(Paths.get(filePath))) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new InputStreamResource(Files.newInputStream(Paths.get(filePath)));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=logs-" + taskId + ".log")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/download")
    @Operation(summary = "Сформировать и скачать лог-файл за указанную дату")
    @ApiResponse(responseCode = "200", description = "Лог-файл успешно сгенерирован и возвращён")
    @ApiResponse(responseCode = "404", description = "Лог-файл не найден или пуст")
    public ResponseEntity<Resource> downloadLogFile(@RequestParam String date) {
        Resource resource = logService.generateAndReturnLogFile(date);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=log-" + date + ".log")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}