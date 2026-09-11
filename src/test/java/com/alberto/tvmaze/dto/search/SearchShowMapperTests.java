package com.alberto.tvmaze.dto.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.alberto.tvmaze.dto.search.external.TvMazeChannel;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.dto.search.external.TvMazeShow;
import java.util.List;
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

        SearchShowResponse response = mapper.toResponses(List.of(new TvMazeSearchResult(0.9, show))).getFirst();

        assertThat(response).isEqualTo(new SearchShowResponse(
                1L, "Example Show", "Network One", "<p>Summary</p>", List.of("Drama")));
    }

    @Test
    void usesWebChannelOrNullWhenNetworkChannelIsUnavailable() {
        TvMazeShow webChannelShow = new TvMazeShow(
                2L, "Web Show", null, new TvMazeChannel("Stream One"), null, List.of());
        TvMazeShow noChannelShow = new TvMazeShow(3L, "No Channel", null, null, null, List.of());

        List<SearchShowResponse> responses = mapper.toResponses(List.of(
                new TvMazeSearchResult(0.8, webChannelShow),
                new TvMazeSearchResult(0.7, noChannelShow)));

        assertThat(responses)
                .extracting(SearchShowResponse::channel)
                .containsExactly("Stream One", null);
    }
}
