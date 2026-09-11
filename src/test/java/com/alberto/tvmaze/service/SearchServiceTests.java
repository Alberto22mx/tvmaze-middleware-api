package com.alberto.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alberto.tvmaze.client.TvMazeClient;
import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.search.SearchShowMapper;
import com.alberto.tvmaze.dto.search.SearchShowResponse;
import com.alberto.tvmaze.dto.search.external.TvMazeChannel;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.dto.search.external.TvMazeShow;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchServiceTests {

    @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private CommentService commentService;

    private final SearchShowMapper searchShowMapper = new SearchShowMapper();

    @Test
    void usesNetworkNameAsChannelAndAddsItsComments() {
        TvMazeShow show = new TvMazeShow(
                1L, "First", new TvMazeChannel("Network One"), new TvMazeChannel("Web One"), "Summary", List.of("Drama"));
        when(tvMazeClient.searchShows("first")).thenReturn(List.of(new TvMazeSearchResult(0.9, show)));
        List<CommentSummaryResponse> comments = List.of(new CommentSummaryResponse("Great show", 5));
        when(commentService.getCommentsByShowIds(List.of(1L))).thenReturn(Map.of(1L, comments));

        SearchService searchService = new SearchService(tvMazeClient, searchShowMapper, commentService);
        List<SearchShowResponse> responses = searchService.searchShows("first");

        assertThat(responses).singleElement().satisfies(response -> {
            assertThat(response.channel()).isEqualTo("Network One");
            assertThat(response.comments()).containsExactlyElementsOf(comments);
        });
        verify(commentService).getCommentsByShowIds(List.of(1L));
    }

    @Test
    void fallsBackToWebChannelAndUsesEmptyCommentsWhenThereAreNone() {
        TvMazeShow show = new TvMazeShow(
                2L, "Second", null, new TvMazeChannel("Web Two"), "Summary", List.of("Comedy"));
        when(tvMazeClient.searchShows("second")).thenReturn(List.of(new TvMazeSearchResult(0.8, show)));
        when(commentService.getCommentsByShowIds(List.of(2L))).thenReturn(Map.of());

        SearchService searchService = new SearchService(tvMazeClient, searchShowMapper, commentService);
        List<SearchShowResponse> responses = searchService.searchShows("second");

        assertThat(responses).singleElement().satisfies(response -> {
            assertThat(response.channel()).isEqualTo("Web Two");
            assertThat(response.comments()).isEmpty();
        });
    }
}
