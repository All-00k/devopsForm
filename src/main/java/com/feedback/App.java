package com.feedback;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {

    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = createServer(port);
        server.start();
        System.out.println("Feedback Application started successfully on port " + port);
        System.out.println("Access the form at: http://localhost:" + port + "/");
    }

    public static HttpServer createServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/submit", new SubmitHandler());
        server.createContext("/health", new HealthHandler());
        server.setExecutor(null);
        return server;
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] responseBytes = loadIndexHtml();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }

        private byte[] loadIndexHtml() throws IOException {
            // Try loading from classpath resources first
            try (InputStream is = getClass().getResourceAsStream("/static/index.html")) {
                if (is != null) {
                    return is.readAllBytes();
                }
            }
            // Fallback to local file system
            File file = new File("index.html");
            if (file.exists()) {
                try (InputStream fis = new FileInputStream(file)) {
                    return fis.readAllBytes();
                }
            }
            return "<h1>Online Feedback Form</h1><p>index.html not found.</p>".getBytes(StandardCharsets.UTF_8);
        }
    }

    static class SubmitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received feedback submission: " + body);

                String responseHtml = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Submission Received</title>" +
                        "<style>body{font-family:sans-serif;display:flex;justify-content:center;align-items:center;height:100vh;background:#f0f4f8;}" +
                        ".card{background:white;padding:30px;border-radius:10px;box-shadow:0 4px 12px rgba(0,0,0,0.1);text-align:center;}" +
                        "a{display:inline-block;margin-top:15px;color:#667eea;text-decoration:none;font-weight:bold;}</style></head>" +
                        "<body><div class='card'><h2>Thank You!</h2><p>Your feedback has been successfully submitted.</p>" +
                        "<a href='/'>&larr; Submit Another Response</a></div></body></html>";

                byte[] responseBytes = responseHtml.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "{\"status\":\"UP\"}";
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
