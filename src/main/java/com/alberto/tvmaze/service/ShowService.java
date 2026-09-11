package com.alberto.tvmaze.service;

import com.alberto.tvmaze.config.CacheProperties;
import com.alberto.tvmaze.document.ShowCacheDocument;
import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.show.ShowResponse;
import com.alberto.tvmaze.dto.show.ShowResponseMapper;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import com.alberto.tvmaze.port.out.TvMazePort;
import com.alberto.tvmaze.repository.ShowCacheRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ShowService {

    private final TvMazePort tvMazePort;
    private final ShowCacheRepository showCacheRepository;
    private final CacheProperties cacheProperties;
    private final CommentService commentService;
    private final ShowResponseMapper showResponseMapper;

    public ShowService(
            TvMazePort tvMazePort,
            ShowCacheRepository showCacheRepository,
            CacheProperties cacheProperties,
            CommentService commentService,
            ShowResponseMapper showResponseMapper) {
        this.tvMazePort = tvMazePort;
        this.showCacheRepository = showCacheRepository;
        this.cacheProperties = cacheProperties;
        this.commentService = commentService;
        this.showResponseMapper = showResponseMapper;
    }

    public ShowResponse getShow(long showId) {
        TvMazeShowDetails show = getCachedOrFetchShow(showId);
        List<CommentSummaryResponse> comments = commentService.getCommentsByShowIds(List.of(showId))
                .getOrDefault(showId, List.of());

        return showResponseMapper.toResponse(show, comments);
    }

    private TvMazeShowDetails getCachedOrFetchShow(long showId) {
        Optional<ShowCacheDocument> cachedShow = showCacheRepository.findById(showId)
                .filter(this::isCacheValid);
        if (cachedShow.isPresent()) {
            return cachedShow.get().show();
        }

        return fetchAndCacheShow(showId);
    }

    private boolean isCacheValid(ShowCacheDocument cachedShow) {
        return cachedShow.expiresAt() != null && cachedShow.expiresAt().isAfter(Instant.now());
    }

    private TvMazeShowDetails fetchAndCacheShow(long showId) {
        TvMazeShowDetails show = tvMazePort.getShow(showId);
        Instant cachedAt = Instant.now();
        Instant expiresAt = cachedAt.plus(Duration.ofHours(cacheProperties.ttlHours()));
        showCacheRepository.save(new ShowCacheDocument(showId, show, cachedAt, expiresAt));
        return show;
    }
}
