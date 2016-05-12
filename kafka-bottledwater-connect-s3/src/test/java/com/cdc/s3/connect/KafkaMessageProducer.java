package com.cdc.s3.connect;


import kafka.admin.AdminUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.codec.DecoderException;
import scala.collection.Seq;
import kafka.utils.ZKStringSerializer$;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class KafkaMessageProducer {

    void createTopic(String topic) {
        String zookeeperConnect = "192.168.33.10:2181";
        int sessionTimeoutMs = 10 * 1000;
        int connectionTimeoutMs = 8 * 1000;
        // Note: You must initialize the ZkClient with ZKStringSerializer.  If you don't, then
        // createTopic() will only seem to work (it will return without error).  The topic will exist in
        // only ZooKeeper and will be returned when listing topics, but Kafka itself does not create the
        // topic.
        ZkClient zkClient = new ZkClient(
                zookeeperConnect,
                sessionTimeoutMs,
                connectionTimeoutMs,
                ZKStringSerializer$.MODULE$);

        // Security for Kafka was added in Kafka 0.9.0.0
        boolean isSecureKafkaCluster = false;
        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), isSecureKafkaCluster);
        Seq<String> allTopics = zkUtils.getAllTopics();
        if (allTopics.contains(topic)) {
            System.out.println("topic = " + topic + " already exists");
            return;
        }

        int partitions = 2;
        int replication = 2;
        AdminUtils.createTopic(zkUtils, topic, partitions, replication, new Properties());

        zkClient.close();
    }

    void producer(Schema schema) throws IOException {

        String topic = "location";
        createTopic(topic);

        Properties props = new Properties();
        props.put("metadata.broker.list", "192.168.33.10:9092");
        props.put("serializer.class", "kafka.serializer.DefaultEncoder");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        Producer<String, byte[]> producer = new Producer<String, byte[]>(config);

        GenericData.Record value = new GenericData.Record(schema);
        value.put("address", "1 city center");
        value.put("address1", "main street");
        value.put("city", "lexington");
        value.put("state", "ma");

        System.out.println("Original Message : "+ value);
        //Step3 : Serialize the object to a bytearray
        DatumWriter<GenericRecord> writer = new SpecificDatumWriter<GenericRecord>(schema);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(value, encoder);
        encoder.flush();
        out.close();

        byte[] serializedBytes = out.toByteArray();
        System.out.println("Sending message in bytes : " + serializedBytes);
        //String serializedHex = Hex.encodeHexString(serializedBytes);
        //System.out.println("Serialized Hex String : " + serializedHex);
        KeyedMessage<String, byte[]> message = new KeyedMessage<String, byte[]>(topic, serializedBytes);
        producer.send(message);
        producer.close();

    }


    public static void main(String[] args) throws IOException, DecoderException {
        KafkaMessageProducer test = new KafkaMessageProducer();
        Schema schema = SchemaBuilder
                .record("address")
                .fields()
                .name("address").type().stringType().noDefault()
                .name("address1").type().stringType().noDefault()
                .name("city").type().stringType().noDefault()
                .name("state").type().stringType().noDefault()
                .endRecord();
        test.producer(schema);
    }
}