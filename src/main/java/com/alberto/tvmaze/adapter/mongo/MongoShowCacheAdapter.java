package com.alberto.tvmaze.adapter.mongo;

import com.alberto.tvmaze.document.ShowCacheDocument;
import com.alberto.tvmaze.port.out.ShowCachePort;
import com.alberto.tvmaze.repository.ShowCacheRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MongoShowCacheAdapter implements ShowCachePort {

    private final ShowCacheRepository showCacheRepository;

    public MongoShowCacheAdapter(ShowCacheRepository showCacheRepository) {
        this.showCacheRepository = showCacheRepository;
    }

    @Override
    public Optional<ShowCacheDocument> findById(Long showId) {
        return showCacheRepository.findById(showId);
    }

    @Override
    public <S extends ShowCacheDocument> S save(S showCacheDocument) {
        return showCacheRepository.save(showCacheDocument);
    }
}
