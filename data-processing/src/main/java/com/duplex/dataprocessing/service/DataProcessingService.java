package com.duplex.dataprocessing.service;

import com.duplex.dataprocessing.domain.CsvFileStatistics;
import com.duplex.dataprocessing.domain.CsvLine;
import com.duplex.dataprocessing.domain.Under18Data;
import com.duplex.dataprocessing.repository.CsvFileStatisticsRepository;
import com.duplex.dataprocessing.repository.CsvLineRepository;
import com.duplex.dataprocessing.repository.Under18DataRepository;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service that listens to ActiveMq queue and processes csv files.
 * Makes call to data collection api to get the file bytes and batch inserts the csv lines to database.
 * It also calculates the average age and height of under 18 records per csv file.
 * **/
@Service
public class DataProcessingService {
    Logger logger = LoggerFactory.getLogger(DataProcessingService.class);
    @Autowired
    private Under18DataRepository under18DataRepository;
    @Autowired
    private CsvFileStatisticsRepository csvFileStatisticsRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CsvLineRepository csvLineRepository;
    @Value("${duplex.datacollection.url}")
    private String dataCollectionUrl;

    private Object findDuplexDattaById(Long id){
        return restTemplate.getForObject(dataCollectionUrl+"/get/"+id, Object.class);
    }

    public Iterable<CsvLine> findAll(){
       return csvLineRepository.findAll();
    }

    @JmsListener(destination="${activemq.filereceived.queue.name}")
    public void processData(TextMessage textMessage){
        Long id = null;
        boolean processingError = false;
        try {
            id = Long.valueOf(textMessage.getText());
        } catch (JMSException e) {
            logger.error("Error reading jms message", e);
        }
        if(id != null && id > 0){
            Map dataMap = (LinkedHashMap)findDuplexDattaById(id);

            if(dataMap != null && dataMap.size() > 0) {
                CsvFileStatistics fileStatistics = new CsvFileStatistics();
                fileStatistics.setDataLoadId(id);
                fileStatistics.setSource((String)dataMap.get("source"));
                csvFileStatisticsRepository.save(fileStatistics);

                try {
                    byte[] fileBytes = DatatypeConverter.parseBase64Binary((String) dataMap.get("fileData"));
                    String source = (String) dataMap.get("source");
                    CSVReader csvReader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(fileBytes)));

                    List<String[]> lines = csvReader.readAll();
                    lines.remove(0);
                    AtomicInteger counter = new AtomicInteger();
                    Collection<List<String[]>> subLists = lines.stream()
                            .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 10))
                            .values();
		            /**This code can be moved to scheduler running on multiple threads depending on the processor and number of
                     * lines in csv file. Each  sublist can be submitted to a runnable and multiple runnables
                     * can be executed by the executor. Code for scheduler can be found in
                     * utils.DataProcessingScheduler class
                     * **/

                    for (List<String[]> listOfLines : subLists) {
                        List<CsvLine> csvLines = new ArrayList<>(listOfLines.size());
                        for (String[] it : listOfLines) {
                            CsvLine csvLine = new CsvLine();
                            csvLine.setAge(Integer.valueOf(it[1]));
                            csvLine.setHeight(Integer.valueOf(it[2]));
                            csvLine.setName(it[0]);
                            csvLine.setDataLoadId(id);
                            csvLine.setCreatedDate(LocalDateTime.now());
                            csvLines.add(csvLine);
                        }
                        csvLineRepository.saveAll(csvLines);
                    }

                    Map<Boolean, List<String[]>> output = lines.stream().collect(Collectors.groupingBy(lineArray -> (Integer.valueOf(lineArray[1]) > 18)));
                    List<String[]> entriesUnder18 = output.get(false);
                    Integer under18Count = entriesUnder18.size();
                    Double averageAge = entriesUnder18.stream().collect(Collectors.averagingInt(lineArray -> (Integer.valueOf(lineArray[1]))));
                    Double averageHeight = entriesUnder18.stream().collect(Collectors.averagingDouble(lineArray -> (Double.valueOf(lineArray[2]))));
                    Under18Data under18Data = new Under18Data();
                    under18Data.setAverageAge(averageAge.intValue());
                    under18Data.setAverageHeight(averageHeight);
                    under18Data.setAliasName(source + LocalDateTime.now());
                    under18Data.setUnder18Count(under18Count);
                    under18Data.setDateCreated(LocalDateTime.now());
                    under18DataRepository.save(under18Data);
                } catch (IOException e) {
                    processingError = true;
                }
                fileStatistics.setProcessed(true);
                fileStatistics.setProcessingError(processingError);
            }
        }
        else{
            logger.error("No record found with id"+id);
        }
    }

    public Map reportingData(){
        Map reportMap = new HashMap<String, Long>(2);
        reportMap.put("FilesReceived", csvFileStatisticsRepository.count());
        reportMap.put("LinesProcessed", csvLineRepository.count());
        return reportMap;
    }
}
