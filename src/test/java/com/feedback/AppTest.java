package com.feedback;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private HttpServer server;
    private int port;

    @BeforeEach
    public void setUp() throws IOException {
        // Start server on random available ephemeral port
        server = App.createServer(0);
        server.start();
        port = server.getAddress().getPort();
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testHealthEndpoint() throws IOException {
        URL url = new URL("http://localhost:" + port + "/health");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode);

        try (InputStream is = conn.getInputStream()) {
            String response = new String(is.readAllBytes());
            assertTrue(response.contains("\"status\":\"UP\""));
        }
    }

    @Test
    public void testFormHtmlEndpoint() throws IOException {
        URL url = new URL("http://localhost:" + port + "/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode);

        try (InputStream is = conn.getInputStream()) {
            String response = new String(is.readAllBytes());
            assertTrue(response.contains("Feedback Form"));
            assertTrue(response.contains("name=\"category\""));
        }
    }
}
