package com.premierleagueanalytics.ingestion;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.io.File;


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

class SparkHttpToAvro {
    private String url = "https://api.football-data.org";

    public SparkHttpToAvro() {
        System.out.println("Spark Http To Avro");
    }

    // maybe separate it out into its own class? Idk how this will scale though.
    // possibly a models class? Benefit of one class per model is we can combine related
    // endpoints and prevent file spread.
    public void getTeams() {
        try {
            String response = httpGetRequest("/v4/teams");
            JsonNode rootNode = parseJson(response);
            JsonNode teamsNode = rootNode.get("teams");

            // TODO: Understand the writing process better
            DatumWriter<TeamInfo> datumWriter = new SpecificDatumWriter<>(TeamInfo.class);
            try (DataFileWriter<TeamInfo> dataFileWriter = new DataFileWriter<>(datumWriter)) {
                dataFileWriter.create(TeamInfo.getClassSchema(), new File("teams.avro"));

                if (teamsNode.isArray()) {
                    for (JsonNode jsonNode : teamsNode) {
                        TeamInfo teamInfo = createTeamInfo(jsonNode);
                        dataFileWriter.append(teamInfo);
                    }
                }
            }

//            System.out.println("Teams Response:\n" + response);
        } catch (URISyntaxException | InterruptedException | IOException e) {
            System.err.println("Request to get teams failed with error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to parse json with error: " + e.getMessage());
        }


    }

    private JsonNode parseJson(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonString);
    }

    private TeamInfo createTeamInfo(JsonNode node) {
        return TeamInfo.newBuilder()
                .setId(node.get("id").asInt())
                .setName(node.get("name").asText())
                .setShortName(node.get("shortName").asText())
                .setTla(node.get("tla").asText())
                .setCrest(node.get("crest").asText())
                .setAddress(node.get("address").asText())
                .setWebsite(node.get("website").asText())
                .setFounded(node.get("founded").asInt())
                .setClubColors(node.get("clubColors").asText())
                .setVenue(node.get("venue").asText())
                .setLastUpdated(Instant.parse(node.get("lastUpdated").asText()).toEpochMilli())
                .build();
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