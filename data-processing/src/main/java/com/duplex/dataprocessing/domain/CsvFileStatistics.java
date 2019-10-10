package com.duplex.dataprocessing.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class CsvFileStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String source;
    private Boolean processed = false;
    private Boolean processingError = false;
    private Long dataLoadId;
}
