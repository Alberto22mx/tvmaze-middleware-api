package com.alberto.tvmaze.controller;

import com.alberto.tvmaze.dto.search.SearchShowResponse;
import com.alberto.tvmaze.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<SearchShowResponse> searchShows(
            @RequestParam("search_query") @NotBlank String searchQuery) {
        return searchService.searchShows(searchQuery);
    }
}
