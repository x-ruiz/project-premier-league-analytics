package com.premierleagueanalytics.ingestion.utils;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.specific.SpecificRecord;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.lang.NoSuchMethodException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.time.Instant;


public class AvroUtils {

    // Handles converting the string timestamp from the API into a long timestamp in proper format
    public static long timestampConversion(String timestamp) {
        // Handle the case that lastUpdated is a null
        long epochTime;

        if (timestamp != "null") {
            epochTime = Instant.parse(timestamp).toEpochMilli();
        } else {
            epochTime = 0;
        }
        return epochTime;
    }

    public static <T extends SpecificRecordBase & SpecificRecord> void writeAvro(
            Class<T> schemaClass,
            String outputAvroPath,
            JsonNode node,
            Function<JsonNode, T> recordCreator) {
        // TODO: Understand the writing process better
        DatumWriter<T> datumWriter = new SpecificDatumWriter<>(schemaClass);
        try (DataFileWriter<T> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(schemaClass.getDeclaredConstructor().newInstance().getSchema(), new File(outputAvroPath));

            if (node.isArray()) {
                for (JsonNode jsonNode : node) {
                    T record = recordCreator.apply(jsonNode);
                    dataFileWriter.append(record);
                }
            }
        } catch (IOException | InstantiationException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            System.out.println("Error writing to avro file");
        }
    }
}