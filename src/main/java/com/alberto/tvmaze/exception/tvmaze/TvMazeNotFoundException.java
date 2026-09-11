package com.alberto.tvmaze.exception.tvmaze;

public class TvMazeNotFoundException extends TvMazeException {

    public TvMazeNotFoundException(Throwable cause) {
        super("TVMaze resource was not found", cause);
    }
}
