package com.alberto.tvmaze.document;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
public record CommentDocument(
        @Id String id,
        long showId,
        String comment,
        int rating,
        Instant createdAt) {
}
