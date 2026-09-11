package com.alberto.tvmaze.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alberto.tvmaze.exception.GlobalExceptionHandler;
import com.alberto.tvmaze.repository.CommentRepository;
import com.alberto.tvmaze.service.CommentService;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CommentControllerTests {

    private CommentService commentService;
    private MockMvc mockMvc;
    private boolean commentSaved;

    @BeforeEach
    void setUp() {
        CommentRepository commentRepository = (CommentRepository) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{CommentRepository.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("save")) {
                        commentSaved = true;
                        return arguments[0];
                    }

                    throw new UnsupportedOperationException(method.getName());
                });
        commentService = new CommentService(commentRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new CommentController(commentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createsCommentWithValidRequest() throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"show_id": 1, "comment": "Great show", "rating": 5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void rejectsRatingAboveFive() throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"show_id": 1, "comment": "Great show", "rating": 6}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.rating")
                        .value("rating must be less than or equal to 5"));

        assertFalse(commentSaved);
    }
}
