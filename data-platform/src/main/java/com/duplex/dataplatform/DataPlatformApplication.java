package com.duplex.dataplatform;

import org.apache.commons.io.IOUtils;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mockserver.MockServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootApplication
@EnableJms
public class DataPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataPlatformApplication.class, args);
        //Mock http server to get csv files
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(9091);
        try {
           File dummyFile =  ResourceUtils.getFile("classpath:data/testdata.csv");
            mockServer.when(HttpRequest.request()
                    .withMethod("POST")
                    .withPath("/data"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withHeaders(
                                    new Header(HttpHeaders.CONTENT_TYPE, "text/csv"),
                                    new Header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"data.csv\"; filename=\"output.csv\"")
                                    )
                            .withBody(Files.readAllBytes(dummyFile.toPath()))
                    );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
