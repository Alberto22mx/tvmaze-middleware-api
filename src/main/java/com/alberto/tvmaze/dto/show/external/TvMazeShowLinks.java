package com.alberto.tvmaze.dto.show.external;

public record TvMazeShowLinks(
        TvMazeLink self,
        TvMazeLink previousepisode,
        TvMazeLink nextepisode) {
}
