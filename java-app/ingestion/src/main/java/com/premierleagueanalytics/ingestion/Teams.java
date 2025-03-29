package com.premierleagueanalytics.ingestion;

import com.premierleagueanalytics.ingestion.TeamInfo;

class Teams {
    public void main(String[] args) {
        String outputAvroPath = "teams.avro";

        try {
            String response = Api.httpGetRequest("/v4/teams?limit=500");
            JsonNode rootNode = Api.parseJson(response);
            JsonNode teamsNode = rootNode.get("teams");

            // Upload AVRO file to gcs bucket
            StorageBucket.uploadObject(outputAvroPath);
        } catch (URISyntaxException | InterruptedException | IOException e) {
            System.err.println("Processing teams data failed with error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to parse json with error: " + e.getMessage());
        }
    }

    private TeamInfo createTeamInfo(JsonNode node) {
        // Handle the case that lastUpdated is a null
        String lastUpdated = node.get("lastUpdated").asText();
        long epochTime;

        if (lastUpdated != "null") {
            epochTime = Instant.parse(lastUpdated).toEpochMilli();
        } else {
            epochTime = 0;
        }

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