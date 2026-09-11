package com.alberto.tvmaze.port.out;

import com.alberto.tvmaze.document.ShowCacheDocument;
import java.util.Optional;

public interface ShowCachePort {

    Optional<ShowCacheDocument> findById(Long showId);

    <S extends ShowCacheDocument> S save(S showCacheDocument);
}
