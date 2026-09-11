package com.alberto.tvmaze.document;

import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "show_cache")
public record ShowCacheDocument(
        @Id long showId,
        TvMazeShowDetails show,
        Instant cachedAt,
        @Indexed(name = "show_cache_expires_at_ttl", expireAfter = "0s") Instant expiresAt) {
}
