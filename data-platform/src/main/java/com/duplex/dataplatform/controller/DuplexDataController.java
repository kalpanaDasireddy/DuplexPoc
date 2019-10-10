package com.duplex.dataplatform.controller;

import com.duplex.dataplatform.domain.DuplexData;
import com.duplex.dataplatform.service.DataCollectionService;
import com.duplex.dataplatform.service.DataPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest Controller to get details of files received from different sources
 * **/
@RestController
public class DuplexDataController {
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private DataPersistenceService dataPersistenceService;

    /**
     * Test method to load data from HTTP source. Should be replaced by scheduler?
     * **/
    @GetMapping("/data")
    public String getData(){
        dataCollectionService.requestData();
        return "HTTP call made";
    }

    /** Rest api invoked by data-processing module to get the csv file by id**/
    @GetMapping
    @RequestMapping("get/{id}")
    public @ResponseBody DuplexData get(@PathVariable Long id){
        return dataPersistenceService.findById(id);
    }

    /**@GetMapping("/list")
    public Iterable<DuplexData> list(){
        return dataProcessorService.findAll();
    }

    @GetMapping("/listUnder18")
    public Iterable<Under18Data> listUnder18(){
        return dataProcessorService.findUnder18();
    }**/

    /** Rest api used by data-processing module to update the status of file processing**/
    @PatchMapping("/updateStatus/{id}")
    public Map updateProcessingStatus(@RequestBody DuplexData duplexData, @PathVariable Long id){
        HashMap map = new HashMap(1);
        boolean updated = dataPersistenceService.updateProcessingStatus(duplexData, id);
        map.put("updated", updated);
        return map;
    }
}
