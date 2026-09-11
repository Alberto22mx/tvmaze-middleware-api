package com.alberto.tvmaze.service;

import com.alberto.tvmaze.client.TvMazeClient;
import com.alberto.tvmaze.dto.search.SearchShowMapper;
import com.alberto.tvmaze.dto.search.SearchShowResponse;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final TvMazeClient tvMazeClient;
    private final SearchShowMapper searchShowMapper;
    private final CommentService commentService;

    public SearchService(
            TvMazeClient tvMazeClient,
            SearchShowMapper searchShowMapper,
            CommentService commentService) {
        this.tvMazeClient = tvMazeClient;
        this.searchShowMapper = searchShowMapper;
        this.commentService = commentService;
    }

    public List<SearchShowResponse> searchShows(String searchQuery) {
        List<TvMazeSearchResult> searchResults = tvMazeClient.searchShows(searchQuery);
        List<Long> showIds = searchResults.stream()
                .map(searchResult -> searchResult.show().id())
                .distinct()
                .toList();

        return searchShowMapper.toResponses(
                searchResults,
                commentService.getCommentsByShowIds(showIds));
    }
}
