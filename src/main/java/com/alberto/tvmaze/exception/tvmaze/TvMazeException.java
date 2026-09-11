package com.alberto.tvmaze.exception.tvmaze;

public abstract class TvMazeException extends RuntimeException {

    protected TvMazeException(String message, Throwable cause) {
        super(message, cause);
    }
}
