package com.duplex.dataprocessing.domain;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class Under18Data {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String aliasName;
    private Integer averageAge;
    private Double averageHeight;
    private LocalDateTime dateCreated;
    private Integer under18Count;
}

