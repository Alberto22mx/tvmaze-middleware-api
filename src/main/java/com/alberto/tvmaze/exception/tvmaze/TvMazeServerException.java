package com.alberto.tvmaze.exception.tvmaze;

public class TvMazeServerException extends TvMazeException {

    public TvMazeServerException(Throwable cause) {
        super("TVMaze server error", cause);
    }
}
