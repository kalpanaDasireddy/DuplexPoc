package com.duplex.dataplatform.domain;

import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.time.LocalDateTime;

@Entity
@Data
public class DuplexData {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime processedDate;
    @Lob
    @Column(columnDefinition="BLOB")
    private byte[] fileData;
    private String source;
    private Boolean processed = false;
    private Long columnCount;
    @Transient
    private String encodedFileData;

    public String getEncodedFileData(){
        return DatatypeConverter.printBase64Binary(fileData);
    }
}
