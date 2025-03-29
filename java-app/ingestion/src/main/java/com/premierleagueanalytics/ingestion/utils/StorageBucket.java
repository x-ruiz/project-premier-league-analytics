package com.premierleagueanalytics.ingestion;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;
import java.nio.file.Paths;

class StorageBucket {
    String projectName = "premier-league-analytics";
    String bucketName = "pla-landing-zone-bkt-us";

    public static void uploadObject(String objectName) throws IOException {
        // The ID of your GCS object
        // String objectName = "your-object-name";

        // The path to your file to upload
        // String filePath = "path/to/your/file"
        LocalDate currentDate = LocalDate.now(ZoneId.of("America/Chicago"));
        filePath = "avro/dt=" + currentDate + "/" + objectName;
        Storage storage = StorageOptions.newBuilder().setProjectId(this.projectName).build().getService();
        BlobId blobId = BlobId.of(this.bucketName, objectName); // identifier for location
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build(); // for us to define metadata about object

        storage.createFrom(blobInfo, Paths.get(filePath));

        System.out.println(
                "File " + filePath + " uploaded to bucket " + this.bucketName + " as " + objectName);
    }

}