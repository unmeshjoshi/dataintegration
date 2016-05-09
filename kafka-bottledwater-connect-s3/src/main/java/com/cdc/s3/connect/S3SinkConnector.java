package com.cdc.s3.connect;

import com.amazonaws.util.StringUtils;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S3SinkConnector extends SinkConnector {
    static final String S3_BUCKET_NAME = "s3_bucket_name";
    private String bucketName;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        bucketName = props.get(S3_BUCKET_NAME);
        if (StringUtils.isNullOrEmpty(bucketName)) {
            bucketName = "dataintegration";// default name
        }
    }

    @Override
    public Class<? extends Task> taskClass() {
        return S3SinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        for (int i = 0; i < maxTasks; i++) {
            Map<String, String> config = new HashMap<>();
            if (bucketName != null)
                config.put(S3_BUCKET_NAME, bucketName);
            configs.add(config);
        }
        return configs;
    }

    @Override
    public void stop() {

    }

}
