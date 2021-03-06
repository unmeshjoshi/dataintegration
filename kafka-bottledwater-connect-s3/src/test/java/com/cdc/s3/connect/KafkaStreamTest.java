package com.cdc.s3.connect;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

public class KafkaStreamTest {

    public static void main(String[] args) {
        Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example");
        // Where to find Kafka broker(s).
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.33.10:9092");
        // Where to find the corresponding ZooKeeper ensemble.
        streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "192.168.33.10:2181");
        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());

        // Set up serializers and deserializers, which we will use for overriding the default serdes
        // specified above.
        final Serde<byte[]> stringSerde = Serdes.ByteArray();
        final Serde<byte[]> longSerde = Serdes.ByteArray();

        // In the subsequent lines we define the processing topology of the Streams application.
        KStreamBuilder builder = new KStreamBuilder();


        KStream<byte[], byte[]> records = builder.stream(stringSerde, stringSerde, "location");

        getStreams(records);


        // Now that we have finished the definition of the processing topology we can actually run
        // it via `start()`.  The Streams application as a whole can be launched just like any
        // normal Java application that has a `main()` method.
        KafkaStreams streams = new KafkaStreams(builder, streamsConfiguration);
        streams.start();
    }

    private static KStream<String, String> getStreams(KStream<byte[], byte[]> records) {
        KStream<String, String> map = records
                .map((key, value) -> {
                    String s = asString(value);
                    String s1 = asString(value);
                    return new KeyValue<>(s, s1);
                });

        return map;
    }

    private static String asString(byte[] value) {
        GenericDatumReader dr = new GenericDatumReader();
        DataFileStream reader = null;
        try {
            reader = new DataFileStream(new ByteArrayInputStream(value), dr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : reader) {
            GenericRecord record = (GenericRecord) o;
            String csv = new GenericRecordWrapper().asCsv(record);
            sb.append(csv);
            sb.append("\n");
        }
        return sb.toString();
    }


}
