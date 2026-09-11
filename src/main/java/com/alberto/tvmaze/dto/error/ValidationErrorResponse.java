package com.alberto.tvmaze.dto.error;

import java.util.Map;

public record ValidationErrorResponse(int status, String message, Map<String, String> errors) {
}
