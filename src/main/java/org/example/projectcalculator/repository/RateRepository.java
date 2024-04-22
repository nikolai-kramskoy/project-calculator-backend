package org.example.projectcalculator.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.example.projectcalculator.model.Rate;

@Repository
public interface RateRepository extends CrudRepository<Rate, Long> {

  Optional<Rate> findByIdAndProjectId(long id, long projectId);

  List<Rate> findAllByProjectId(Long projectId);
}
