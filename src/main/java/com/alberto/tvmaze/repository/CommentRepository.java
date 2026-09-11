package com.alberto.tvmaze.repository;

import com.alberto.tvmaze.document.CommentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<CommentDocument, String> {
}
