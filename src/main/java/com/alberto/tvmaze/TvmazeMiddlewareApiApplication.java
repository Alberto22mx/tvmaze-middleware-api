package com.alberto.tvmaze;

import com.alberto.tvmaze.config.CacheProperties;
import com.alberto.tvmaze.config.TvMazeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        TvMazeProperties.class,
        CacheProperties.class
})
public class TvmazeMiddlewareApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TvmazeMiddlewareApiApplication.class, args);
    }

}
