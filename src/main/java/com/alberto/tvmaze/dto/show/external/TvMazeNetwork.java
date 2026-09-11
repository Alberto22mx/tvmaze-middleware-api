package com.alberto.tvmaze.dto.show.external;

public record TvMazeNetwork(
        long id,
        String name,
        TvMazeCountry country,
        String officialSite) {
}
