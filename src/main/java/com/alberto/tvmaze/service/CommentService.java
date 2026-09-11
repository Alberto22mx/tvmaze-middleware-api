package com.alberto.tvmaze.service;

import com.alberto.tvmaze.document.CommentDocument;
import com.alberto.tvmaze.dto.comment.CommentResponse;
import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.comment.CreateCommentRequest;
import com.alberto.tvmaze.repository.CommentRepository;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentResponse createComment(CreateCommentRequest request) {
        commentRepository.save(new CommentDocument(
                null,
                request.show_id(),
                request.comment(),
                request.rating(),
                Instant.now()));

        return new CommentResponse("CREATED");
    }

    public Map<Long, List<CommentSummaryResponse>> getCommentsByShowIds(Collection<Long> showIds) {
        if (showIds.isEmpty()) {
            return Map.of();
        }

        return commentRepository.findByShowIdIn(showIds).stream()
                .collect(Collectors.groupingBy(
                        CommentDocument::showId,
                        Collectors.mapping(
                                comment -> new CommentSummaryResponse(comment.comment(), comment.rating()),
                                Collectors.toList())));
    }
}
