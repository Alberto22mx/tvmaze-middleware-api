package com.alberto.tvmaze.dto.error;

import java.util.Map;

public record ValidationErrorResponse(String message, Map<String, String> errors) {
}
