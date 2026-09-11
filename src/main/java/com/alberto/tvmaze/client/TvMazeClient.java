package com.alberto.tvmaze.client;

import com.alberto.tvmaze.config.TvMazeProperties;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TvMazeClient {

    private final RestClient restClient;

    public TvMazeClient(TvMazeProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public List<TvMazeSearchResult> searchShows(String searchQuery) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/shows")
                        .queryParam("q", searchQuery)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public TvMazeShowDetails getShow(long showId) {
        return restClient.get()
                .uri("/shows/{showId}", showId)
                .retrieve()
                .body(TvMazeShowDetails.class);
    }
}
