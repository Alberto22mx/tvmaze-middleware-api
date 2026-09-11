package com.alberto.tvmaze.client;

import com.alberto.tvmaze.config.TvMazeProperties;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import com.alberto.tvmaze.dto.search.external.TvMazeSearchResult;
import com.alberto.tvmaze.exception.tvmaze.TvMazeConnectionException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeNotFoundException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeRateLimitException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeServerException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeTimeoutException;
import com.alberto.tvmaze.port.out.TvMazePort;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;

@Component
public class TvMazeClient implements TvMazePort {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

    private final RestClient restClient;

    public TvMazeClient(TvMazeProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        this.restClient = createRestClient(properties.baseUrl(), requestFactory);
    }

    TvMazeClient(RestClient restClient) {
        this.restClient = restClient;
    }

    private RestClient createRestClient(String baseUrl, SimpleClientHttpRequestFactory requestFactory) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public List<TvMazeSearchResult> searchShows(String searchQuery) {
        return execute(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/shows")
                        .queryParam("q", searchQuery)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                }));
    }

    @Override
    public TvMazeShowDetails getShow(long showId) {
        return execute(() -> restClient.get()
                .uri("/shows/{showId}", showId)
                .retrieve()
                .body(TvMazeShowDetails.class));
    }

    private <T> T execute(Supplier<T> request) {
        try {
            return request.get();
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new TvMazeNotFoundException(exception);
            }
            if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new TvMazeRateLimitException(exception);
            }
            throw exception;
        } catch (HttpServerErrorException exception) {
            throw new TvMazeServerException(exception);
        } catch (ResourceAccessException exception) {
            if (isTimeout(exception)) {
                throw new TvMazeTimeoutException(exception);
            }
            throw new TvMazeConnectionException(exception);
        }
    }

    private boolean isTimeout(ResourceAccessException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof SocketTimeoutException || cause instanceof HttpTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
