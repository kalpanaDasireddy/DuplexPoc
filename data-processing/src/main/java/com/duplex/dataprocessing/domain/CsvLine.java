package com.duplex.dataprocessing.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class CsvLine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private Integer height;
    private Boolean validEntry = true;
    private LocalDateTime createdDate;
    private Boolean passedToPresentation = false;
    private String dataSource;
    private Long dataLoadId;
}
