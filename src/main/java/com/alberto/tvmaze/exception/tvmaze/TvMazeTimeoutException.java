package com.alberto.tvmaze.exception.tvmaze;

public class TvMazeTimeoutException extends TvMazeException {

    public TvMazeTimeoutException(Throwable cause) {
        super("TVMaze request timed out", cause);
    }
}
