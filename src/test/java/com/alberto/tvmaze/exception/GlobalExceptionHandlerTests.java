package com.alberto.tvmaze.exception;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alberto.tvmaze.controller.ShowController;
import com.alberto.tvmaze.exception.tvmaze.TvMazeConnectionException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeNotFoundException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeRateLimitException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeServerException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeTimeoutException;
import com.alberto.tvmaze.service.ShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTests {

    @Mock
    private ShowService showService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ShowController(showService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsNotFoundJsonForTvMaze404() throws Exception {
        assertError(new TvMazeNotFoundException(new RuntimeException()), 404, "TVMaze resource was not found");
    }

    @Test
    void returnsRateLimitJsonForTvMaze429() throws Exception {
        assertError(new TvMazeRateLimitException(new RuntimeException()), 429, "TVMaze rate limit was exceeded");
    }

    @Test
    void returnsUnavailableJsonForTvMaze5xxAndConnectionErrors() throws Exception {
        assertError(new TvMazeServerException(new RuntimeException()), 503, "TVMaze server error");
        assertError(new TvMazeConnectionException(new RuntimeException()), 503, "Unable to connect to TVMaze");
    }

    @Test
    void returnsGatewayTimeoutJsonForTvMazeTimeout() throws Exception {
        assertError(new TvMazeTimeoutException(new RuntimeException()), 504, "TVMaze request timed out");
    }

    private void assertError(RuntimeException exception, int expectedStatus, String expectedMessage) throws Exception {
        doThrow(exception).when(showService).getShow(1);

        mockMvc.perform(get("/show").param("show_id", "1"))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
}
