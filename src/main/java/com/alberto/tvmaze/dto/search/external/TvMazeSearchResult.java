package com.alberto.tvmaze.dto.search.external;

public record TvMazeSearchResult(
        double score,
        TvMazeShow show) {
}
