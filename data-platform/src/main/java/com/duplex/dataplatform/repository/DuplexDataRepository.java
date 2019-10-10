package com.duplex.dataplatform.repository;

import com.duplex.dataplatform.domain.DuplexData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuplexDataRepository extends CrudRepository<DuplexData, Long> {

}
