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

    @Test
    public void shouldCreateS3FileNameFromPartitionAndOffset() {
        String fileNameSuffix = String.format("%s-%05d-%012d", "address", 0, 15);
        assertEquals("dataIntegration_address-00000-000000000015", "dataIntegration_" + fileNameSuffix);
    }
}