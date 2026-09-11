package com.alberto.tvmaze.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tvmaze")
public record TvMazeProperties(String baseUrl) {
}
