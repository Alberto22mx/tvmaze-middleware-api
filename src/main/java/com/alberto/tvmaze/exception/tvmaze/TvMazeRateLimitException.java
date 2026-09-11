package com.alberto.tvmaze.exception.tvmaze;

public class TvMazeRateLimitException extends TvMazeException {

    public TvMazeRateLimitException(Throwable cause) {
        super("TVMaze rate limit was exceeded", cause);
    }
}
