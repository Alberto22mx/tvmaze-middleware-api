package com.alberto.tvmaze.dto.show.external;

import java.util.List;

public record TvMazeShowDetails(
        long id,
        String url,
        String name,
        String type,
        String language,
        List<String> genres,
        String status,
        Integer runtime,
        Integer averageRuntime,
        String premiered,
        String ended,
        String officialSite,
        TvMazeSchedule schedule,
        TvMazeRating rating,
        int weight,
        TvMazeNetwork network,
        TvMazeNetwork webChannel,
        TvMazeCountry dvdCountry,
        TvMazeExternals externals,
        TvMazeImage image,
        String summary,
        Long updated,
        TvMazeShowLinks _links) {
}
