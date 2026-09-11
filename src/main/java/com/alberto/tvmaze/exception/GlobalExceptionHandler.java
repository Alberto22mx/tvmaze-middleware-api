package com.alberto.tvmaze.exception;

import com.alberto.tvmaze.dto.error.ErrorResponse;
import com.alberto.tvmaze.dto.error.ValidationErrorResponse;
import com.alberto.tvmaze.exception.tvmaze.TvMazeConnectionException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeNotFoundException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeRateLimitException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeServerException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        return new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
    }

    @ExceptionHandler(TvMazeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTvMazeNotFound(TvMazeNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(TvMazeRateLimitException.class)
    public ResponseEntity<ErrorResponse> handleTvMazeRateLimit(TvMazeRateLimitException exception) {
        return error(HttpStatus.TOO_MANY_REQUESTS, exception);
    }

    @ExceptionHandler({TvMazeServerException.class, TvMazeConnectionException.class})
    public ResponseEntity<ErrorResponse> handleTvMazeUnavailable(TvMazeException exception) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, exception);
    }

    @ExceptionHandler(TvMazeTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTvMazeTimeout(TvMazeTimeoutException exception) {
        return error(HttpStatus.GATEWAY_TIMEOUT, exception);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, TvMazeException exception) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), exception.getMessage()));
    }
}
