package com.example.delivery.service;

import java.util.concurrent.CompletableFuture;


public interface LogService {

    CompletableFuture<String> generateLogFileForDateAsync(String date);

    String getTaskStatus(String taskId);

    String getLogFilePath(String taskId);

}
