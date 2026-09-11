package com.alberto.tvmaze.port.out;

import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import java.util.List;

public interface TvMazePort {

    List<TvMazeSearchResult> searchShows(String searchQuery);

    TvMazeShowDetails getShow(long showId);
}
