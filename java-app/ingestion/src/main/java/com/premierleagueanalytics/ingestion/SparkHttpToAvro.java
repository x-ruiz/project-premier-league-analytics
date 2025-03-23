package com.premierleagueanalytics.ingestion;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

// Exception Imports
import java.io.IOException;
import java.net.URISyntaxException;
import java.lang.InterruptedException;

class SparkHttpToAvro {
    private String url = "https://api.football-data.org";

    public SparkHttpToAvro() {
        System.out.println("Spark Http To Avro");
    }

    public void getTeams() {
        try {
            String response = httpGetRequest("/v4/teams");
            System.out.println("Teams Response:\n" + response);
        } catch (URISyntaxException | InterruptedException | IOException e) {
            System.err.println("Request to get teams failed with error: " + e.getMessage());
        }
    }

    // TODO: Extract this method into its own API class
    private String httpGetRequest(String endpoint) throws URISyntaxException, IOException, InterruptedException {
        String url = this.url + endpoint;
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