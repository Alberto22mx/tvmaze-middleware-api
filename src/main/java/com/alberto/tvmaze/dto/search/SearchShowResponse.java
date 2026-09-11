package com.alberto.tvmaze.dto.search;

import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import java.util.List;

public record SearchShowResponse(
        long id,
        String name,
        String channel,
        String summary,
        List<String> genres,
        List<CommentSummaryResponse> comments) {
}
