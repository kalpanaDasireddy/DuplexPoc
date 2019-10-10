package com.duplex.dataplatform.service;

import com.duplex.dataplatform.domain.DuplexData;
import com.duplex.dataplatform.repository.DuplexDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service to save the files received by duplex data platform and send a message to an active mq queue for further processing
 */
@Service
public class DataPersistenceService {
    private static final Logger logger = LoggerFactory.getLogger(DataCollectionService.class);
    @Autowired
    private DuplexDataRepository duplexDataRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${activemq.filereceived.queue.name}")
    private String senderQueueName;

    /** Method to save file data to database and send a message to data processing service with id of the database row**/
    public Long saveData(byte[] blobData, String source){
        DuplexData duplexData = new DuplexData();
        duplexData.setFileData(blobData);
        duplexData.setCreateDate(LocalDateTime.now());
        duplexData.setSource(source);
        duplexDataRepository.save(duplexData);
        jmsTemplate.setDefaultDestinationName(senderQueueName);
        jmsTemplate.send(new MessageCreator() {
            @Override
            public TextMessage createMessage(Session session) throws JMSException {
                return session.createTextMessage(duplexData.getId().toString());
            }
        });
        return duplexData.getId();
    }

    /**  Service used by data processing service to get file details by id**/
    public DuplexData findById(Long id){
        DuplexData duplexData = null;
        Optional<DuplexData> optional = duplexDataRepository.findById(id);
        if(optional.isPresent()){
            duplexData = optional.get();
        }
        else{
            logger.error("DuplexData.findById : No data found with the given identifier"+id);
        }
        return duplexData;
    }

    /** Service used by data processing service to mark the file as processed.**/
    public boolean updateProcessingStatus(DuplexData duplexData, Long id){
        Optional<DuplexData> optional = duplexDataRepository.findById(id);
        Boolean updated = false;
        if(optional.isPresent()) {
            DuplexData duplexData1 = optional.get();
            duplexData1.setProcessed(true);
            duplexData1.setColumnCount(duplexData.getColumnCount());
            duplexDataRepository.save(duplexData1);
            updated = true;
        }
        else{
            logger.error("updateProcessingStatus:No data found with the given identifier"+id);
        }
        return updated;
    }
}
