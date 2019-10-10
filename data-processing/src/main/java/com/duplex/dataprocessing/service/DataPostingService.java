package com.duplex.dataprocessing.service;

import com.duplex.dataprocessing.domain.CsvLine;
import com.duplex.dataprocessing.repository.CsvLineRepository;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;

/** Spring scheduler service that runs every second to send entries over 18 to presentation service.
 * Note: Can be made async depending on the data presentation service implementation**/
@Service
public class DataPostingService {
    private Logger logger = LoggerFactory.getLogger(DataPostingService.class);

    @Autowired
    private CsvLineRepository csvLineRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(fixedRate = 1000)
    public void postToPresentation(){
        Optional<CsvLine> optional = csvLineRepository.findTopByAgeGreaterThanAndValidEntryAndPassedToPresentationOrderByCreatedDateAsc(18,true, false);
        if(optional.isPresent()) {
            CsvLine csvLine = optional.get();
            JSONObject json = new JSONObject();
            json.put("name", csvLine.getName());
            json.put("age", csvLine.getAge());
            json.put("height", csvLine.getHeight());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);
           HttpStatus response =  restTemplate.exchange("http://localhost:9092/person/create", HttpMethod.POST, entity, String.class).getStatusCode();

           if(response.compareTo(HttpStatus.OK) == 0){
               csvLine.setPassedToPresentation(true);
               csvLineRepository.save(csvLine);
           }
           else{
               logger.error("Unable to send data to presentation service");
           }

        }
    }
}
