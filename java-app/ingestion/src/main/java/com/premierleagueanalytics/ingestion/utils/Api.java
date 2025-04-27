package com.premierleagueanalytics.ingestion.utils;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Exception Imports
import java.io.IOException;
import java.net.URISyntaxException;
import java.lang.InterruptedException;

public class Api {
    private static String baseUrl = "https://api.football-data.org";

    public static JsonNode parseJson(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonString);
    }


    // TODO: Extract this method into its own API class
    public static String httpGetRequest(String endpoint) throws URISyntaxException, IOException, InterruptedException {
        String url = baseUrl + endpoint;
        String apiKey = System.getenv("API_KEY");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .header("X-Auth-Token", apiKey)
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("HTTP request failed with status code: " + response.statusCode());
                return null;
            }
        } catch (URISyntaxException e) {
            System.err.println(e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            System.err.println("Http Request interrupted:" + e.getMessage());
            throw e;
        }
    }
}