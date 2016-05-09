package com.cdc.s3.connect;

import com.amazonaws.util.StringUtils;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class S3SinkTask extends SinkTask {
    private String bucketName;
    private List<SinkRecord> buffer = new ArrayList<>();//TODO maintain state on s3
    private int flushSize = 5;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        resetOffsetsToZero(context.assignment());
        bucketName = props.get(S3SinkConnector.S3_BUCKET_NAME);
    }

    private void resetOffsetsToZero(Set<TopicPartition> assignment) {
        for (TopicPartition topicPartition : assignment) {
            context.offset(topicPartition, 0);
        }
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        //Keep in buffer till it reaches flushSize. This is just for demo purpose.
        //Because Kafka is inherently distributed system, we can not keep buffer in memory like this,
        //the state needs to be kept in distributed storage, possibly, in the same target system,(in this case s3)
        //we also need to allow recovery on startup by keeping the offset till which records are successfully put on s3.
        if (buffer.size() < flushSize) {
            buffer.addAll(records);
            return;
        }

        AwsGateway gateway = new AwsGateway();
        try {
            String content = toString(buffer);

            if (!StringUtils.isNullOrEmpty(content)) {
                System.out.print("Uploading to s3");
                System.out.println(content);
                String fileName = s3FileName(buffer);
                gateway.uploadToS3(bucketName, fileName, content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer = new ArrayList<>();
    }

    private String s3FileName(List<SinkRecord> buffer1) {
        SinkRecord firstRecord = buffer1.get(0);
        String fileNameSuffix = String.format("%s-%05d-%012d", firstRecord.topic(), firstRecord.kafkaPartition(), firstRecord.kafkaOffset());
        return "dataIntegration_" + fileNameSuffix;
    }

    private String toString(Collection<SinkRecord> records) throws IOException {
        StringBuilder content = new StringBuilder();
        for (SinkRecord record : records) {
            String value = new StructWrapper().asCsv((org.apache.kafka.connect.data.Struct) record.value());
            content.append(value);
            content.append("\n");
        }
        return content.toString();
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) {

        for (TopicPartition topicPartition : offsets.keySet()) {
            System.out.println(topicPartition);
        }
    }

    @Override
    public void stop() {

    }
}
