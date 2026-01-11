package com.example.demo.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class ApiClient {
    private ApiClient() {}

    private static final int TIMEOUT_SEC = 10;
    private static final int RETRIES = 1;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SEC))
            .build();

    private static String baseUrl() {
        String v = ClientProps.getOr("api.baseUrl", null);
        if (v == null) v = ClientProps.getOr("backend.baseUrl", null);
        if (v == null) v = ClientProps.getOr("baseUrl", "http://localhost:8080");
        return v.endsWith("/") ? v.substring(0, v.length() - 1) : v;
    }

    public static HttpResponse<String> get(String path) throws Exception {
        String url = baseUrl() + path;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(TIMEOUT_SEC))
                .header("Accept", "application/json")
                .GET()
                .build();
        return sendWithRetry(url, req);
    }


    

    public static HttpResponse<String> post(String path, String json) throws Exception {
        String url = baseUrl() + path;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(TIMEOUT_SEC))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendWithRetry(url, req);
    }

    public static HttpResponse<String> put(String path, String json) throws Exception {
        String url = baseUrl() + path;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(TIMEOUT_SEC))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendWithRetry(url, req);
    }

    public static HttpResponse<String> delete(String path) throws Exception {
        String url = baseUrl() + path;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(TIMEOUT_SEC))
                .header("Accept", "application/json")
                .DELETE()
                .build();
        return sendWithRetry(url, req);
    }

    private static HttpResponse<String> sendWithRetry(String url, HttpRequest req) throws Exception {
        try {
            return CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            // For connection/timeout errors, retry once quickly
            if (RETRIES > 0 && (e instanceof java.net.ConnectException || 
                e instanceof java.net.http.HttpTimeoutException)) {
                Thread.sleep(100L);
                return CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            }
            throw e;
        }
    }
}
