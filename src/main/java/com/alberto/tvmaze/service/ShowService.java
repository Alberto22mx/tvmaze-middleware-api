package com.alberto.tvmaze.service;

import com.alberto.tvmaze.client.TvMazeClient;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import org.springframework.stereotype.Service;

@Service
public class ShowService {

    private final TvMazeClient tvMazeClient;

    public ShowService(TvMazeClient tvMazeClient) {
        this.tvMazeClient = tvMazeClient;
    }

    public TvMazeShowDetails getShow(long showId) {
        return tvMazeClient.getShow(showId);
    }
}
