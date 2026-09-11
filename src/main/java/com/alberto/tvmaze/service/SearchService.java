package com.alberto.tvmaze.service;

import com.alberto.tvmaze.client.TvMazeClient;
import com.alberto.tvmaze.dto.search.SearchShowMapper;
import com.alberto.tvmaze.dto.search.SearchShowResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final TvMazeClient tvMazeClient;
    private final SearchShowMapper searchShowMapper;

    public SearchService(TvMazeClient tvMazeClient, SearchShowMapper searchShowMapper) {
        this.tvMazeClient = tvMazeClient;
        this.searchShowMapper = searchShowMapper;
    }

    public List<SearchShowResponse> searchShows(String searchQuery) {
        return searchShowMapper.toResponses(tvMazeClient.searchShows(searchQuery));
    }
}
