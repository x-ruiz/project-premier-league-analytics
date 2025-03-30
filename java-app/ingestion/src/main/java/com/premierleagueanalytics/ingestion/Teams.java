package com.premierleagueanalytics.ingestion;

import com.premierleagueanalytics.ingestion.TeamInfo;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

// Exception Imports
import java.io.IOException;
import java.net.URISyntaxException;
import java.lang.InterruptedException;


class Teams {
    public static void main(String[] args) {
        String outputAvroPath = "ingestion/teams.avro";
        String gcsAvroFileName = "teams.avro";

        try {
            String response = Api.httpGetRequest("/v4/teams?limit=500");
//            System.out.println("response " + response);
            JsonNode rootNode = Api.parseJson(response);
            JsonNode teamsNode = rootNode.get("teams");

            // Create recordCreator function wrapper of the method
            // TODO: Need to understand this Function wrapper more in detail
            Function<JsonNode, TeamInfo> createTeamInfoFunction = jsonNode -> createTeamInfo(jsonNode);

            // Parse response and write records to files
            AvroUtils.writeAvro(TeamInfo.class, outputAvroPath, teamsNode, createTeamInfoFunction);

            // Upload AVRO file to gcs bucket
            StorageBucket.uploadObject(gcsAvroFileName, outputAvroPath, "teams");
        } catch (URISyntaxException | InterruptedException | IOException e) {
            System.err.println("Processing teams data failed with error: " + e);
        } catch (Exception e) {
            System.err.println("Failed to parse json with error: " + e.getMessage());
        }
    }

    // Handles transposing the jsonNode response from the API into the Avro generated class
    public static TeamInfo createTeamInfo(JsonNode node) {
        String lastUpdated = node.get("lastUpdated").asText();
        long epochTime = AvroUtils.timestampConversion(lastUpdated);

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
                .setLastUpdated(epochTime)
                .build();
    }
}