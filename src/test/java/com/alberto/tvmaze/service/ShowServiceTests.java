package com.alberto.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alberto.tvmaze.client.TvMazeClient;
import com.alberto.tvmaze.config.CacheProperties;
import com.alberto.tvmaze.document.ShowCacheDocument;
import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.show.ShowResponse;
import com.alberto.tvmaze.dto.show.ShowResponseMapper;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import com.alberto.tvmaze.repository.ShowCacheRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShowServiceTests {

    @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private ShowCacheRepository showCacheRepository;

    @Mock
    private CommentService commentService;

    @Test
    void returnsCachedShowAndLoadsCommentsWithoutCallingTvMaze() {
        long showId = 42L;
        TvMazeShowDetails cachedShow = show(showId, "Cached Show");
        List<CommentSummaryResponse> comments = List.of(new CommentSummaryResponse("Excellent", 5));
        when(showCacheRepository.findById(showId)).thenReturn(Optional.of(new ShowCacheDocument(
                showId, cachedShow, Instant.now(), Instant.now().plusSeconds(3600))));
        when(commentService.getCommentsByShowIds(List.of(showId))).thenReturn(Map.of(showId, comments));

        ShowService showService = new ShowService(
                tvMazeClient, showCacheRepository, new CacheProperties(24), commentService, new ShowResponseMapper());
        ShowResponse response = showService.getShow(showId);

        assertThat(response.id()).isEqualTo(showId);
        assertThat(response.name()).isEqualTo("Cached Show");
        assertThat(response.comments()).containsExactlyElementsOf(comments);
        verify(commentService).getCommentsByShowIds(List.of(showId));
        verify(tvMazeClient, never()).getShow(any(Long.class));
        verify(showCacheRepository, never()).save(any());
    }

    @Test
    void fetchesAndCachesShowOnCacheMiss() {
        long showId = 7L;
        TvMazeShowDetails fetchedShow = show(showId, "Fetched Show");
        when(showCacheRepository.findById(showId)).thenReturn(Optional.empty());
        when(tvMazeClient.getShow(showId)).thenReturn(fetchedShow);
        when(commentService.getCommentsByShowIds(List.of(showId))).thenReturn(Map.of());

        ShowService showService = new ShowService(
                tvMazeClient, showCacheRepository, new CacheProperties(24), commentService, new ShowResponseMapper());
        ShowResponse response = showService.getShow(showId);

        ArgumentCaptor<ShowCacheDocument> cachedDocument = ArgumentCaptor.forClass(ShowCacheDocument.class);
        verify(tvMazeClient).getShow(showId);
        verify(showCacheRepository).save(cachedDocument.capture());
        assertThat(cachedDocument.getValue().showId()).isEqualTo(showId);
        assertThat(cachedDocument.getValue().show()).isSameAs(fetchedShow);
        assertThat(response.id()).isEqualTo(showId);
        assertThat(response.name()).isEqualTo("Fetched Show");
        assertThat(response.comments()).isEmpty();
    }

    private TvMazeShowDetails show(long id, String name) {
        return new TvMazeShowDetails(
                id, "https://example.test/shows/" + id, name, null, null, List.of(), null, null, null,
                null, null, null, null, null, 0, null, null, null, null, null, null, null, null);
    }
}
