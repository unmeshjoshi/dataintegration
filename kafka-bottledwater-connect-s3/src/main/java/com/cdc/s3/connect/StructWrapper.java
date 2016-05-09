package com.cdc.s3.connect;

import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;

import java.io.IOException;
import java.util.List;

class StructWrapper {

    public String asCsv(Struct record) throws IOException {
        StringBuilder csv = new StringBuilder();
        String line = line(record);
        csv.append(line);
        csv.append("\n");
        return csv.toString();

    }

    private String line(Struct record) {
        StringBuilder csv = new StringBuilder();
        org.apache.kafka.connect.data.Schema schema = record.schema();
        List<Field> fields = schema.fields();
        for (Field field : fields) {
            csv.append(record.get(field.name()));
            csv.append(",");
        }
        return csv.toString();
    }

}
