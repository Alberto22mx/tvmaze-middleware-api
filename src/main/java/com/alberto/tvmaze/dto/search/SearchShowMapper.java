package com.alberto.tvmaze.dto.search;

import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.dto.search.external.TvMazeShow;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SearchShowMapper {

    public List<SearchShowResponse> toResponses(
            List<TvMazeSearchResult> searchResults,
            Map<Long, List<CommentSummaryResponse>> commentsByShowId) {
        return searchResults.stream()
                .map(searchResult -> toResponse(searchResult, commentsByShowId))
                .toList();
    }

    private SearchShowResponse toResponse(
            TvMazeSearchResult searchResult,
            Map<Long, List<CommentSummaryResponse>> commentsByShowId) {
        TvMazeShow show = searchResult.show();

        return new SearchShowResponse(
                show.id(),
                show.name(),
                resolveChannel(show),
                show.summary(),
                show.genres(),
                commentsByShowId.getOrDefault(show.id(), List.of()));
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
