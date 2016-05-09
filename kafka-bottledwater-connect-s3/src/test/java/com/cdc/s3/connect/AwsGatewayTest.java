package com.cdc.s3.connect;

import com.amazonaws.util.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class AwsGatewayTest {

    @Test
    public void shouldCreateTempFileWithGivenContent() throws IOException {
        AwsGateway gateway = new AwsGateway();
        File file = gateway.tempFile("name", "content");
        String content = IOUtils.toString(new FileInputStream(file));
        assertEquals(content, "content");
    }
}