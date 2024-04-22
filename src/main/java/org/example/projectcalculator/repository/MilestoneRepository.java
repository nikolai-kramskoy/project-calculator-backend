package org.example.projectcalculator.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.example.projectcalculator.model.Milestone;

@Repository
public interface MilestoneRepository extends CrudRepository<Milestone, Long> {

  Optional<Milestone> findByIdAndProjectId(long id, long projectId);

  List<Milestone> findAllByProjectId(long projectId);
}
