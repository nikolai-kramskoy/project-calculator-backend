package org.example.projectcalculator.repository;

import java.util.List;
import org.example.projectcalculator.model.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

  @Query("""
      SELECT DISTINCT project
      FROM Project project
      LEFT JOIN FETCH project.rates
      WHERE project.creator.id = :creatorId
      """)
  List<Project> findAllWithRates(@Param("creatorId") long creatorId);

  @Query("""
      SELECT DISTINCT project
      FROM Project project
      LEFT JOIN FETCH project.teamMembers
      WHERE project.creator.id = :creatorId
      """)
  List<Project> findAllWithTeamMembers(@Param("creatorId") long creatorId);
}
