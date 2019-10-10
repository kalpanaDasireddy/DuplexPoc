package com.duplex.dataplatform.service;

import com.duplex.dataplatform.domain.DuplexData;
import com.duplex.dataplatform.repository.DuplexDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;

/**
 * Service to collect data with HTTP call to source.
 * TODO: Mock webservice is used. Authentication (401) and unsuccessful error (500)handling is not implemented
 * **/
@Service
public class DataCollectionService {
    private static final Logger logger = LoggerFactory.getLogger(DataCollectionService.class);

    @Value("${duplex.datacollection.httpsource}")
    private String httpUrl;
    private DataPersistenceService dataPersistenceService;
    public DataCollectionService(DataPersistenceService dataPersistenceService){
        this.dataPersistenceService = dataPersistenceService;
    }

    public void requestData(){
        StringBuffer stringBuffer = new StringBuffer();
        String data = null;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            InputStream inputStream = connection.getInputStream();
            byte[] fileBytes = inputStream.readAllBytes();
            //replace with enum for full implementation
            dataPersistenceService.saveData(fileBytes, "MockHTTPServer");
        } catch (MalformedURLException e) {
            logger.error("Invalid URL"+httpUrl);
        } catch (IOException e) {
            logger.error("Error parsing response", e);
        }
    }
}


