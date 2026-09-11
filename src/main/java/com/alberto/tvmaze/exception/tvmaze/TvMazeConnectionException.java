package com.alberto.tvmaze.exception.tvmaze;

public class TvMazeConnectionException extends TvMazeException {

    public TvMazeConnectionException(Throwable cause) {
        super("Unable to connect to TVMaze", cause);
    }
}
