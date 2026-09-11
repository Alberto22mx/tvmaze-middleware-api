package com.alberto.tvmaze.dto.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentDTO(
        @NotNull(message = "show_id is required") Long show_id,
        @NotBlank(message = "comment is required") String comment,
        @NotNull(message = "rating is required")
        @Min(value = 0, message = "rating must be greater than or equal to 0")
        @Max(value = 5, message = "rating must be less than or equal to 5")
        Integer rating) {
}
