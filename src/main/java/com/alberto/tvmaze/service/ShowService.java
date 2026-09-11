package com.alberto.tvmaze.service;

import com.alberto.tvmaze.client.TvMazeClient;
import com.alberto.tvmaze.config.CacheProperties;
import com.alberto.tvmaze.document.ShowCacheDocument;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import com.alberto.tvmaze.repository.ShowCacheRepository;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class ShowService {

    private final TvMazeClient tvMazeClient;
    private final ShowCacheRepository showCacheRepository;
    private final CacheProperties cacheProperties;

    public ShowService(TvMazeClient tvMazeClient, ShowCacheRepository showCacheRepository, CacheProperties cacheProperties) {
        this.tvMazeClient = tvMazeClient;
        this.showCacheRepository = showCacheRepository;
        this.cacheProperties = cacheProperties;
    }

    public TvMazeShowDetails getShow(long showId) {
        return showCacheRepository.findById(showId)
                .map(ShowCacheDocument::show)
                .orElseGet(() -> fetchAndCacheShow(showId));
    }

    private TvMazeShowDetails fetchAndCacheShow(long showId) {
        TvMazeShowDetails show = tvMazeClient.getShow(showId);
        Instant cachedAt = Instant.now();
        Instant expiresAt = cachedAt.plus(Duration.ofHours(cacheProperties.ttlHours()));
        showCacheRepository.save(new ShowCacheDocument(showId, show, cachedAt, expiresAt));
        return show;
    }
}
