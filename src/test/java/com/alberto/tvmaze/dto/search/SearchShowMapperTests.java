package com.alberto.tvmaze.dto.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.search.external.TvMazeChannel;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.dto.search.external.TvMazeShow;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SearchShowMapperTests {

    private final SearchShowMapper mapper = new SearchShowMapper();

    @Test
    void mapsOnlyPublicFieldsAndPrefersNetworkChannel() {
        TvMazeShow show = new TvMazeShow(
                1L,
                "Example Show",
                new TvMazeChannel("Network One"),
                new TvMazeChannel("Stream One"),
                "<p>Summary</p>",
                List.of("Drama"));

        SearchShowResponse response = mapper.toResponses(
                List.of(new TvMazeSearchResult(0.9, show)),
                Map.of(1L, List.of(new CommentSummaryResponse("Great show", 5))))
                .getFirst();

        assertThat(response).isEqualTo(new SearchShowResponse(
                1L,
                "Example Show",
                "Network One",
                "<p>Summary</p>",
                List.of("Drama"),
                List.of(new CommentSummaryResponse("Great show", 5))));
    }

    @Test
    void usesWebChannelOrNullWhenNetworkChannelIsUnavailable() {
        TvMazeShow webChannelShow = new TvMazeShow(
                2L, "Web Show", null, new TvMazeChannel("Stream One"), null, List.of());
        TvMazeShow noChannelShow = new TvMazeShow(3L, "No Channel", null, null, null, List.of());

        List<SearchShowResponse> responses = mapper.toResponses(List.of(
                new TvMazeSearchResult(0.8, webChannelShow),
                new TvMazeSearchResult(0.7, noChannelShow)), Map.of());

        assertThat(responses)
                .extracting(SearchShowResponse::channel)
                .containsExactly("Stream One", null);

        assertThat(responses)
                .extracting(SearchShowResponse::comments)
                .containsExactly(List.of(), List.of());
    }
}
