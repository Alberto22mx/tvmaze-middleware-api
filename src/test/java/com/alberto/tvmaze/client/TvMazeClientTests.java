package com.alberto.tvmaze.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alberto.tvmaze.config.TvMazeProperties;
import com.alberto.tvmaze.exception.tvmaze.TvMazeConnectionException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeNotFoundException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeRateLimitException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeServerException;
import com.alberto.tvmaze.exception.tvmaze.TvMazeTimeoutException;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

class TvMazeClientTests {

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void convertsNotFoundResponse() throws IOException {
        TvMazeClient client = clientRespondingWith(404);

        assertThatThrownBy(() -> client.getShow(1))
                .isInstanceOf(TvMazeNotFoundException.class);
    }

    @Test
    void convertsRateLimitResponse() throws IOException {
        TvMazeClient client = clientRespondingWith(429);

        assertThatThrownBy(() -> client.getShow(1))
                .isInstanceOf(TvMazeRateLimitException.class);
    }

    @Test
    void convertsServerErrorResponse() throws IOException {
        TvMazeClient client = clientRespondingWith(500);

        assertThatThrownBy(() -> client.getShow(1))
                .isInstanceOf(TvMazeServerException.class);
    }

    @Test
    void convertsTimeout() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/shows/1", exchange -> {
            try {
                Thread.sleep(250);
                exchange.sendResponseHeaders(200, -1);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        server.start();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(50));
        requestFactory.setReadTimeout(Duration.ofMillis(50));
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl())
                .requestFactory(requestFactory)
                .build();

        assertThatThrownBy(() -> new TvMazeClient(restClient).getShow(1))
                .isInstanceOf(TvMazeTimeoutException.class);
    }

    @Test
    void convertsConnectionError() throws IOException {
        int unusedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            unusedPort = socket.getLocalPort();
        }
        TvMazeClient client = new TvMazeClient(new TvMazeProperties("http://127.0.0.1:" + unusedPort));

        assertThatThrownBy(() -> client.getShow(1))
                .isInstanceOf(TvMazeConnectionException.class);
    }

    private TvMazeClient clientRespondingWith(int status) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/shows/1", exchange -> {
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
        });
        server.start();

        return new TvMazeClient(new TvMazeProperties(baseUrl()));
    }

    private String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }
}
