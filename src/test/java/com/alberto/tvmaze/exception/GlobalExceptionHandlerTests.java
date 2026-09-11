package com.alberto.tvmaze.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.alberto.tvmaze.dto.error.ErrorResponse;
import com.alberto.tvmaze.exception.tvmaze.TvMazeConnectionException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeNotFoundException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeRateLimitException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeServerException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsNotFoundTo404() {
        assertError(
                handler.handleTvMazeNotFound(new TvMazeNotFoundException(new RuntimeException())),
                HttpStatus.NOT_FOUND,
                "TVMaze resource was not found");
    }

    @Test
    void mapsRateLimitTo429() {
        assertError(
                handler.handleTvMazeRateLimit(new TvMazeRateLimitException(new RuntimeException())),
                HttpStatus.TOO_MANY_REQUESTS,
                "TVMaze rate limit was exceeded");
    }

    @Test
    void mapsServerAndConnectionFailuresTo503() {
        assertError(
                handler.handleTvMazeUnavailable(new TvMazeServerException(new RuntimeException())),
                HttpStatus.SERVICE_UNAVAILABLE,
                "TVMaze server error");
        assertError(
                handler.handleTvMazeUnavailable(new TvMazeConnectionException(new RuntimeException())),
                HttpStatus.SERVICE_UNAVAILABLE,
                "Unable to connect to TVMaze");
    }

    @Test
    void mapsTimeoutTo504() {
        assertError(
                handler.handleTvMazeTimeout(new TvMazeTimeoutException(new RuntimeException())),
                HttpStatus.GATEWAY_TIMEOUT,
                "TVMaze request timed out");
    }

    private void assertError(ResponseEntity<ErrorResponse> response, HttpStatus status, String message) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isEqualTo(new ErrorResponse(status.value(), message));
    }
}
