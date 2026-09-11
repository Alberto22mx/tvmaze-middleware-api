package com.alberto.tvmaze.dto.search.external;

import java.util.List;

public record TvMazeShow(
        long id,
        String name,
        TvMazeChannel network,
        TvMazeChannel webChannel,
        String summary,
        List<String> genres) {
}
