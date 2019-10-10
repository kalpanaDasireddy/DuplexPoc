package com.duplex.datapresentation.service;

import com.duplex.datapresentation.domain.PersonDetails;
import com.duplex.datapresentation.repository.PersonDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonDetailsService {

    @Autowired
    private PersonDetailsRepository personDetailsRepository;

    public Long createPerson(PersonDetails personDetails){
        personDetails.setCreatedDate(LocalDateTime.now());
        personDetailsRepository.save(personDetails);
       return personDetails.getId();
    }

    public List<PersonDetails> findAll(){
        Iterable<PersonDetails> iterable =  personDetailsRepository.findAll();
        List<PersonDetails> personDetailsList = new ArrayList<>();
        iterable.forEach(personDetailsList::add);
        return  personDetailsList;
    }
}
