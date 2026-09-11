package com.alberto.tvmaze.dto.search;

import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.dto.search.external.TvMazeShow;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SearchShowMapper {

    public List<SearchShowResponse> toResponses(List<TvMazeSearchResult> searchResults) {
        return searchResults.stream()
                .map(this::toResponse)
                .toList();
    }

    private SearchShowResponse toResponse(TvMazeSearchResult searchResult) {
        TvMazeShow show = searchResult.show();

        return new SearchShowResponse(
                show.id(),
                show.name(),
                resolveChannel(show),
                show.summary(),
                show.genres());
    }

    private String resolveChannel(TvMazeShow show) {
        if (show.network() != null && show.network().name() != null) {
            return show.network().name();
        }

        if (show.webChannel() != null && show.webChannel().name() != null) {
            return show.webChannel().name();
        }

        return null;
    }
}
