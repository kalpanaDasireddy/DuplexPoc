package com.duplex.dataprocessing.utils;

import com.duplex.dataprocessing.domain.CsvLine;
import com.duplex.dataprocessing.repository.CsvLineRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class DataProcessingScheduler {
    @Autowired
    private CsvLineRepository csvLineRepository;

    public void processData(Collection<List<String[]>> csvFiles, Long dataLoadId){
        //number of threads can be configured and read from config server depending on processor
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<Future<Boolean>> responseList = new ArrayList<>();
        for (List<String[]> listOfLines : csvFiles) {
            Future<Boolean> response = executorService.submit(new CsvLineThread(listOfLines, dataLoadId));
            responseList.add(response);        }
        //wait for all threads to complete before shutting down executor. TODO: handle exceptions and return processing status false to be updated to database
        Boolean tasksIncomplete = false;
        while(tasksIncomplete) {
            for (Future<Boolean> future : responseList) {
                try {
                    if (!future.get()) {
                        tasksIncomplete = false;
                        break;
                    }
                    else{
                        tasksIncomplete = true;
                    }
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                }
            }
        }
        executorService.shutdown();
    }

    class CsvLineThread implements Callable<Boolean>{
        private List<String[]> linesToProcess;
        private Long dataLoadId;

        public CsvLineThread(List<String[]> linesToProcess, Long dataLoadId){
            this.linesToProcess = linesToProcess;
            this.dataLoadId = dataLoadId;
        }

        @Override
        public Boolean call() throws Exception {
            List<CsvLine> csvLines = new ArrayList<>(linesToProcess.size());

            for (String[] it : linesToProcess) {
                    CsvLine csvLine = new CsvLine();
                    csvLine.setAge(Integer.valueOf(it[1]));
                    csvLine.setHeight(Integer.valueOf(it[2]));
                    csvLine.setName(it[0]);
                    csvLine.setDataLoadId(dataLoadId);
                    csvLine.setCreatedDate(LocalDateTime.now());
                    csvLines.add(csvLine);
                }
                csvLineRepository.saveAll(csvLines);
            return  true;
        }
    }
}
