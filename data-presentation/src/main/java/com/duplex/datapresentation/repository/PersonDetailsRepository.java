package com.duplex.datapresentation.repository;

import com.duplex.datapresentation.domain.PersonDetails;
import org.springframework.data.repository.CrudRepository;

public interface PersonDetailsRepository extends CrudRepository<PersonDetails, Long> {

}
