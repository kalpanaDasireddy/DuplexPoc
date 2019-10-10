package com.duplex.dataprocessing.controller;

import com.duplex.dataprocessing.domain.CsvLine;
import com.duplex.dataprocessing.service.DataPostingService;
import com.duplex.dataprocessing.service.DataProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Rest controller to get reporting data **/
@RestController
@RequestMapping("/data")
public class DataProcessingController {
    @Autowired
    private DataProcessingService dataProcessingService;

    @GetMapping("/report")
    public Map report(){
        return dataProcessingService.reportingData();
    }
}
