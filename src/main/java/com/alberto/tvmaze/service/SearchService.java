package com.alberto.tvmaze.service;

import com.alberto.tvmaze.dto.search.SearchShowMapper;
import com.alberto.tvmaze.dto.search.SearchShowResponse;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.port.out.TvMazePort;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final TvMazePort tvMazePort;
    private final SearchShowMapper searchShowMapper;
    private final CommentService commentService;

    public SearchService(
            TvMazePort tvMazePort,
            SearchShowMapper searchShowMapper,
            CommentService commentService) {
        this.tvMazePort = tvMazePort;
        this.searchShowMapper = searchShowMapper;
        this.commentService = commentService;
    }

    public List<SearchShowResponse> searchShows(String searchQuery) {
        List<TvMazeSearchResult> searchResults = tvMazePort.searchShows(searchQuery);
        List<Long> showIds = searchResults.stream()
                .map(searchResult -> searchResult.show().id())
                .distinct()
                .toList();

        return searchShowMapper.toResponses(
                searchResults,
                commentService.getCommentsByShowIds(showIds));
    }
}
