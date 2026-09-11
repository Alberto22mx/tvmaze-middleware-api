package com.alberto.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alberto.tvmaze.config.CacheProperties;
import com.alberto.tvmaze.document.ShowCacheDocument;
import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.show.ShowResponse;
import com.alberto.tvmaze.dto.show.ShowResponseMapper;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import com.alberto.tvmaze.port.out.ShowCachePort;
import com.alberto.tvmaze.port.out.TvMazePort;
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
    private TvMazePort tvMazePort;

    @Mock
    private ShowCachePort showCachePort;

    @Mock
    private CommentService commentService;

    @Test
    void returnsCachedShowAndLoadsCommentsWithoutCallingTvMaze() {
        long showId = 42L;
        TvMazeShowDetails cachedShow = show(showId, "Cached Show");
        List<CommentSummaryResponse> comments = List.of(new CommentSummaryResponse("Excellent", 5));
        when(showCachePort.findById(showId)).thenReturn(Optional.of(new ShowCacheDocument(showId, cachedShow, Instant.now(), Instant.now().plusSeconds(3600))));
        when(commentService.getCommentsByShowIds(List.of(showId))).thenReturn(Map.of(showId, comments));

        ShowService showService = new ShowService(tvMazePort, showCachePort, new CacheProperties(24), commentService, new ShowResponseMapper());
        ShowResponse response = showService.getShow(showId);

        assertThat(response.id()).isEqualTo(showId);
        assertThat(response.name()).isEqualTo("Cached Show");
        assertThat(response.comments()).containsExactlyElementsOf(comments);
        verify(commentService).getCommentsByShowIds(List.of(showId));
        verify(tvMazePort, never()).getShow(any(Long.class));
        verify(showCachePort, never()).save(any());
    }

    @Test
    void fetchesAndCachesShowOnCacheMiss() {
        long showId = 7L;
        TvMazeShowDetails fetchedShow = show(showId, "Fetched Show");
        when(showCachePort.findById(showId)).thenReturn(Optional.empty());
        when(tvMazePort.getShow(showId)).thenReturn(fetchedShow);
        when(commentService.getCommentsByShowIds(List.of(showId))).thenReturn(Map.of());

        ShowService showService = new ShowService(
                tvMazePort, showCachePort, new CacheProperties(24), commentService, new ShowResponseMapper());
        ShowResponse response = showService.getShow(showId);

        ArgumentCaptor<ShowCacheDocument> cachedDocument = ArgumentCaptor.forClass(ShowCacheDocument.class);
        verify(tvMazePort).getShow(showId);
        verify(showCachePort).save(cachedDocument.capture());
        assertThat(cachedDocument.getValue().showId()).isEqualTo(showId);
        assertThat(cachedDocument.getValue().show()).isSameAs(fetchedShow);
        assertThat(response.id()).isEqualTo(showId);
        assertThat(response.name()).isEqualTo("Fetched Show");
        assertThat(response.comments()).isEmpty();
    }

    @Test
    void fetchesAndReplacesExpiredCachedShow() {
        long showId = 8L;
        TvMazeShowDetails expiredShow = show(showId, "Expired Show");
        TvMazeShowDetails refreshedShow = show(showId, "Refreshed Show");
        when(showCachePort.findById(showId)).thenReturn(Optional.of(new ShowCacheDocument(
                showId, expiredShow, Instant.now().minusSeconds(7200), Instant.now().minusSeconds(3600))));
        when(tvMazePort.getShow(showId)).thenReturn(refreshedShow);
        when(commentService.getCommentsByShowIds(List.of(showId))).thenReturn(Map.of());

        ShowService showService = new ShowService(
                tvMazePort, showCachePort, new CacheProperties(24), commentService, new ShowResponseMapper());
        ShowResponse response = showService.getShow(showId);

        ArgumentCaptor<ShowCacheDocument> cachedDocument = ArgumentCaptor.forClass(ShowCacheDocument.class);
        verify(tvMazePort).getShow(showId);
        verify(showCachePort).save(cachedDocument.capture());
        assertThat(cachedDocument.getValue().show()).isSameAs(refreshedShow);
        assertThat(cachedDocument.getValue().expiresAt()).isAfter(Instant.now());
        assertThat(response.name()).isEqualTo("Refreshed Show");
    }

    private TvMazeShowDetails show(long id, String name) {
        return new TvMazeShowDetails(
                id, "https://example.test/shows/" + id, name, null, null, List.of(), null, null, null,
                null, null, null, null, null, 0, null, null, null, null, null, null, null, null);
    }
}
