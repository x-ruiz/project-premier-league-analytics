package com.premierleagueanalytics.ingestion;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

class AvroUtils {
    public static <T extends SpecificRecrodBase> writeAvro(Class<T> schemaClass, String outputAvroPath, JsonNode node, Function<JsonNode, T> recordCreator) {
        // TODO: Understand the writing process better
        DatumWriter<T> datumWriter = new SpecificDatumWriter<>(schemaClass);
        try (DataFileWriter<T> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(schemaClass.getClassSchema(), new File(outputAvroPath));

            if (node.isArray()) {
                for (JsonNode jsonNode : node) {
                    T record = recordCreator.apply(jsonNode);
                    dataFileWriter.append(record);
                }
            }
        }
    }
}