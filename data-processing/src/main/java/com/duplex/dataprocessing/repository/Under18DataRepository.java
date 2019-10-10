package com.duplex.dataprocessing.repository;

import com.duplex.dataprocessing.domain.Under18Data;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Under18DataRepository extends CrudRepository<Under18Data, Long> {
}
