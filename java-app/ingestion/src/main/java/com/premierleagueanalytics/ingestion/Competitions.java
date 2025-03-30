package com.premierleagueanalytics.ingestion;

import com.premierleagueanalytics.ingestion.CompetitionInfo;
import com.premierleagueanalytics.ingestion.Area;
import com.premierleagueanalytics.ingestion.CurrentSeason;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

// Exception Imports
import java.io.IOException;
import java.net.URISyntaxException;
import java.lang.InterruptedException;


class Competitions {
    public static void main(String[] args) {
        String outputAvroPath = "ingestion/competitions.avro";
        String gcsAvroFileName = "competitions.avro";

        try {
            String response = Api.httpGetRequest("/v4/competitions");
//            System.out.println("response " + response);
            JsonNode rootNode = Api.parseJson(response);
            JsonNode competitionNode = rootNode.get("competitions");

            // Create recordCreator function wrapper of the method
            // TODO: Need to understand this Function wrapper more in detail
            Function<JsonNode, CompetitionInfo> createCompetitionInfoFunction = jsonNode -> createCompetitionInfo(jsonNode);

            // Parse response and write records to files
            AvroUtils.writeAvro(CompetitionInfo.class, outputAvroPath, competitionNode, createCompetitionInfoFunction);

            // Upload AVRO file to gcs bucket
            StorageBucket.uploadObject(gcsAvroFileName, outputAvroPath, "competitions");
        } catch (URISyntaxException | InterruptedException | IOException e) {
            System.err.println("Processing competitions data failed with error: " + e);
        } catch (Exception e) {
            System.err.println("Failed to parse json with error: " + e.getMessage());
        }
    }

    // Handles transposing the jsonNode response from the API into the Avro generated class
    public static CompetitionInfo createCompetitionInfo(JsonNode node) {
        String lastUpdated = node.get("lastUpdated").asText();
        long epochTime = AvroUtils.timestampConversion(lastUpdated);

        // Deconstruct Area subrecord
        JsonNode areaNode = node.get("area");
        Area areaInfo = Area.newBuilder()
                .setId(areaNode.get("id").asInt())
                .setName(areaNode.get("name").asText())
                .setCode(areaNode.get("code").asText())
                .setFlag(areaNode.get("flag").asText())
                .build();

        // Deconstruct CurrentSeason subrecord
        JsonNode currentSeasonNode = node.get("currentSeason");
        CurrentSeason currentSeasonInfo = CurrentSeason.newBuilder()
                .setId(currentSeasonNode.get("id").asInt())
                .setStartDate(currentSeasonNode.get("startDate").asText())
                .setEndDate(currentSeasonNode.get("endDate").asText())
                .setCurrentMatchday(currentSeasonNode.get("currentMatchday").asInt())
                .setWinner(currentSeasonNode.get("winner").asText())
                .build();


        return CompetitionInfo.newBuilder()
                .setId(node.get("id").asInt())
                .setArea(areaInfo)
                .setName(node.get("name").asText())
                .setCode(node.get("code").asText())
                .setType(node.get("type").asText())
                .setEmblem(node.get("emblem").asText())
                .setPlan(node.get("plan").asText())
                .setCurrentSeason(currentSeasonInfo)
                .setNumberOfAvailableSeasons(node.get("numberOfAvailableSeasons").asInt())
                .setLastUpdated(epochTime)
                .build();
    }
}