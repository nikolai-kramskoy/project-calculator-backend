package org.example.projectcalculator.repository;

import java.util.List;
import java.util.Optional;
import org.example.projectcalculator.model.Feature;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends CrudRepository<Feature, Long> {

  Optional<Feature> findByIdAndProjectId(long id, long projectId);

  List<Feature> findAllByProjectIdAndMilestoneId(long projectId, long milestoneId);

  List<Feature> findAllByProjectId(long projectId);
}
