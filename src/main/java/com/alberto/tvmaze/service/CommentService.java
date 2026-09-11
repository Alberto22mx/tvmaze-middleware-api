package com.alberto.tvmaze.service;

import com.alberto.tvmaze.document.CommentDocument;
import com.alberto.tvmaze.dto.comment.CommentResponse;
import com.alberto.tvmaze.dto.comment.CreateCommentRequest;
import com.alberto.tvmaze.repository.CommentRepository;
import java.time.Instant;
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
}
