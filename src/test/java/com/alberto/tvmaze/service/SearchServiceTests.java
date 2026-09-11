package com.alberto.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.alberto.tvmaze.client.TvMazeClient;
import com.alberto.tvmaze.config.TvMazeProperties;
import com.alberto.tvmaze.document.CommentDocument;
import com.alberto.tvmaze.dto.search.SearchShowMapper;
import com.alberto.tvmaze.dto.search.SearchShowResponse;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.dto.search.external.TvMazeShow;
import com.alberto.tvmaze.repository.CommentRepository;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchServiceTests {

    @Test
    void retrievesCommentsWithOneBatchQuery() {
        List<TvMazeSearchResult> searchResults = List.of(
                new TvMazeSearchResult(0.9, new TvMazeShow(1L, "First", null, null, null, List.of())),
                new TvMazeSearchResult(0.8, new TvMazeShow(2L, "Second", null, null, null, List.of())));
        BatchCommentRepository batchCommentRepository = new BatchCommentRepository();
        CommentRepository commentRepository = batchCommentRepository.createProxy();
        CommentService commentService = new CommentService(commentRepository);
        TvMazeClient tvMazeClient = new TvMazeClient(new TvMazeProperties("https://api.tvmaze.com")) {
            @Override
            public List<TvMazeSearchResult> searchShows(String searchQuery) {
                return searchResults;
            }
        };
        SearchService searchService = new SearchService(tvMazeClient, new SearchShowMapper(), commentService);

        List<SearchShowResponse> responses = searchService.searchShows("show");

        assertThat(batchCommentRepository.queryCount).isOne();
        assertThat(batchCommentRepository.showIds).containsExactlyInAnyOrder(1L, 2L);
        assertThat(responses.getFirst().comments()).hasSize(1);
        assertThat(responses.get(1).comments()).isEmpty();
    }

    private static class BatchCommentRepository {

        private int queryCount;
        private Collection<Long> showIds;

        private CommentRepository createProxy() {
            return (CommentRepository) Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class<?>[]{CommentRepository.class},
                    (proxy, method, arguments) -> {
                        if (method.getName().equals("findByShowIdIn")) {
                            queryCount++;
                            showIds = ((Collection<?>) arguments[0]).stream()
                                    .map(Long.class::cast)
                                    .toList();
                            return List.of(new CommentDocument(
                                    "comment-id", 1L, "Great show", 5, Instant.now()));
                        }

                        throw new UnsupportedOperationException(method.getName());
                    });
        }
    }
}
