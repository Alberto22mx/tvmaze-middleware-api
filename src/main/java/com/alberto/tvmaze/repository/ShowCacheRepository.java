package com.alberto.tvmaze.repository;

import com.alberto.tvmaze.document.ShowCacheDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowCacheRepository extends MongoRepository<ShowCacheDocument, Long> {
}
