package com.alberto.tvmaze.repository;

import com.alberto.tvmaze.document.CommentDocument;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<CommentDocument, String> {

    List<CommentDocument> findByShowIdIn(Collection<Long> showIds);
}
