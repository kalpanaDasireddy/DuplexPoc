package com.duplex.dataprocessing.repository;

import com.duplex.dataprocessing.domain.CsvLine;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CsvLineRepository extends CrudRepository<CsvLine, Long> {

    public Optional<CsvLine> findTopByAgeGreaterThanAndValidEntryAndPassedToPresentationOrderByCreatedDateAsc(@Param("age") int age, @Param("validEntry") boolean validEntry,
                                                                     @Param("passedToPresentation") boolean passedToPresentation);
}
