package com.premierleagueanalytics.ingestion;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.time.LocalDate;
import java.time.ZoneId;

import java.io.IOException;
import java.nio.file.Paths;

class StorageBucket {
    static String projectName = "premier-league-analytics";
    static String bucketName = "pla-landing-zone-bkt-us";

    public static void uploadObject(String objectName, String sourcePath) throws IOException {
        // The ID of your GCS object
        // String objectName = "your-object-name";

        // The path to your file to upload
        // String filePath = "path/to/your/file"
        LocalDate currentDate = LocalDate.now(ZoneId.of("America/Chicago"));
        String objectPath = "avro/dt=" + currentDate + "/" + objectName;
        Storage storage = StorageOptions.newBuilder().setProjectId(projectName).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectPath); // identifier for location
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build(); // for us to define metadata about object

        storage.createFrom(blobInfo, Paths.get(sourcePath));

        System.out.println(
                "File " + sourcePath + " uploaded to bucket " + bucketName + " as " + objectPath);
    }

}