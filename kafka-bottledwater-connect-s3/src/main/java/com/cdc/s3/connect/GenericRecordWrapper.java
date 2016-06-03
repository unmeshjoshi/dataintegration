package com.cdc.s3.connect;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.util.List;

public class GenericRecordWrapper {

    public String asCsv(GenericRecord record) {
        StringBuilder csv = new StringBuilder();
        String line = line(record);
        csv.append(line);
        csv.append("\n");
        return csv.toString();

    }

    private String line(GenericRecord record) {
        StringBuilder csv = new StringBuilder();
        Schema schema = record.getSchema();
        List<Schema.Field> fields = schema.getFields();
        for (Schema.Field field : fields) {
            csv.append(record.get(field.name()));
            csv.append(",");
        }
        return csv.toString();
    }

}
