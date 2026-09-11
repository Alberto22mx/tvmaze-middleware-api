package com.alberto.tvmaze.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alberto.tvmaze.exception.GlobalExceptionHandler;
import com.alberto.tvmaze.service.CommentService;
import com.alberto.tvmaze.dto.comment.CommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CommentControllerTests {

    @Mock
    private CommentService commentService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CommentController(commentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void acceptsRatingBoundaries() throws Exception {
        when(commentService.createComment(any())).thenReturn(new CommentResponse("CREATED"));

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"show_id": 1, "comment": "Great show", "rating": 0}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"));

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"show_id": 1, "comment": "Great show", "rating": 5}
                                """))
                .andExpect(status().isCreated());

        verify(commentService, org.mockito.Mockito.times(2)).createComment(any());
    }

    @Test
    void rejectsInvalidCommentPayloads() throws Exception {
        assertInvalid("{\"show_id\": 1, \"comment\": \"Great show\", \"rating\": -1}", "rating",
                "rating must be greater than or equal to 0");
        assertInvalid("{\"show_id\": 1, \"comment\": \"Great show\", \"rating\": 6}", "rating",
                "rating must be less than or equal to 5");
        assertInvalid("{\"show_id\": 1, \"comment\": \"Great show\", \"rating\": null}", "rating",
                "rating is required");
        assertInvalid("{\"show_id\": 1, \"comment\": \"\", \"rating\": 3}", "comment", "comment is required");
        assertInvalid("{\"show_id\": 1, \"comment\": null, \"rating\": 3}", "comment", "comment is required");
        assertInvalid("{\"show_id\": 1, \"comment\": \"   \", \"rating\": 3}", "comment", "comment is required");
        assertInvalid("{\"show_id\": null, \"comment\": \"Great show\", \"rating\": 3}", "show_id", "show_id is required");

        verify(commentService, never()).createComment(any());
    }

    private void assertInvalid(String payload, String field, String message) throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors." + field).value(message));
    }
}
