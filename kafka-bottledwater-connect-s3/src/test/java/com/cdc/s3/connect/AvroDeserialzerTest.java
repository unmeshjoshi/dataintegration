package com.cdc.s3.connect;


import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class AvroDeserialzerTest {

    @Test
    public void shouldDeserializeAvroStreamWithoutSchema() throws IOException {
        Schema schema = SchemaBuilder
                .record("address")
                .fields()
                .name("address").type().stringType().noDefault()
                .name("address1").type().stringType().noDefault()
                .name("city").type().stringType().noDefault()
                .name("state").type().stringType().noDefault()
                .endRecord();

        GenericData.Record value = new GenericData.Record(schema);
        value.put("address", "1 city center");
        value.put("address1", "main street");
        value.put("city", "waltham");
        value.put("state", "ma");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>();
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(writer).create(schema, baos);
        dataFileWriter.append(value);
        dataFileWriter.flush();
        assertNotEquals(baos.size(), 0);
        printGenericRecord(baos.toByteArray());
    }

    private void printGenericRecord(byte[] is) throws IOException {

        GenericDatumReader dr=new GenericDatumReader();
        DataFileStream reader=new DataFileStream(new ByteArrayInputStream(is),dr);
        Schema schema = reader.getSchema();
        for (Object o : reader) {
            GenericRecord record = (GenericRecord)o;
            log(record);
        }

     }

    private void log(GenericRecord record) {
        Schema schema = record.getSchema();
        List<Schema.Field> fields = schema.getFields();
        for (Schema.Field field : fields) {
            System.out.println(record.get(field.name()));
        }
    }
}
