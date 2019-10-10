package com.duplex.datapresentation.controller;

import com.duplex.datapresentation.domain.PersonDetails;
import com.duplex.datapresentation.service.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Rest controller to create processed data entries  **/
@RestController
@RequestMapping("/person")
public class PersonDetailsController {

    @Autowired
    private PersonDetailsService personDetailsService;

    @PostMapping("/create")
    public Map create(@RequestBody PersonDetails personDetails){
        Map responseMap = new HashMap(1);
        Long personId  = personDetailsService.createPerson(personDetails);
        if(personId != null && personId > 0)
            responseMap.put("status", 200);
        else
            responseMap.put("status", 500);
        return responseMap;
    }

    @GetMapping("/list")
    public List<PersonDetails> list() {
        return personDetailsService.findAll();
    }
}
