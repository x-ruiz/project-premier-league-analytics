package com.premierleagueanalytics.ingestion;

import com.premierleagueanalytics.ingestion.AreaInfo;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

// Exception Imports
import java.io.IOException;
import java.net.URISyntaxException;
import java.lang.InterruptedException;


class Areas {
    public static void main(String[] args) {
        String outputAvroPath = "areas.avro";

        try {
            String response = Api.httpGetRequest("/v4/areas");
//            System.out.println("response " + response);
            JsonNode rootNode = Api.parseJson(response);
            JsonNode areaNode = rootNode.get("areas");

            // Create recordCreator function wrapper of the method
            // TODO: Need to understand this Function wrapper more in detail
            Function<JsonNode, AreaInfo> createAreaInfoFunction = jsonNode -> createAreaInfo(jsonNode);

            // Parse response and write records to files
            AvroUtils.writeAvro(AreaInfo.class, outputAvroPath, areaNode, createAreaInfoFunction);

            // Upload AVRO file to gcs bucket
            StorageBucket.uploadObject(outputAvroPath, "areas");
        } catch (URISyntaxException | InterruptedException | IOException e) {
            System.err.println("Processing areas data failed with error: " + e);
        } catch (Exception e) {
            System.err.println("Failed to parse json with error: " + e.getMessage());
        }
    }

    // Handles transposing the jsonNode response from the API into the Avro generated class
    public static AreaInfo createAreaInfo(JsonNode node) {
        return AreaInfo.newBuilder()
                .setId(node.get("id").asInt())
                .setName(node.get("name").asText())
                .setCountryCode(node.get("countryCode").asText())
                .setFlag(node.get("flag").asText())
                .setParentAreaId(node.get("parentAreaId").asInt())
                .setParentArea(node.get("parentArea").asText())
                .build();
    }
}