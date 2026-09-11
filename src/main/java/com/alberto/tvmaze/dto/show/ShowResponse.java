package com.alberto.tvmaze.dto.show;

import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.show.external.TvMazeCountry;
import com.alberto.tvmaze.dto.show.external.TvMazeExternals;
import com.alberto.tvmaze.dto.show.external.TvMazeImage;
import com.alberto.tvmaze.dto.show.external.TvMazeNetwork;
import com.alberto.tvmaze.dto.show.external.TvMazeRating;
import com.alberto.tvmaze.dto.show.external.TvMazeSchedule;
import com.alberto.tvmaze.dto.show.external.TvMazeShowLinks;
import java.util.List;

public record ShowResponse(
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
        TvMazeShowLinks _links,
        List<CommentSummaryResponse> comments) {
}
