package com.premierleagueanalytics.ingestion;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;


// Avro Related
import com.premierleagueanalytics.ingestion.TeamInfo;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Exception Imports
import java.io.IOException;
import java.net.URISyntaxException;
import java.lang.InterruptedException;

class Utils {
    private String url = "https://api.football-data.org";

    private JsonNode parseJson(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonString);
    }

    private void writeAvro() {
        // TODO: Understand the writing process better
        DatumWriter<TeamInfo> datumWriter = new SpecificDatumWriter<>(TeamInfo.class);
        try (DataFileWriter<TeamInfo> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(TeamInfo.getClassSchema(), new File(outputAvroPath));

            if (teamsNode.isArray()) {
                for (JsonNode jsonNode : teamsNode) {
                    TeamInfo teamInfo = createTeamInfo(jsonNode);
                    dataFileWriter.append(teamInfo);
                }
            }
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