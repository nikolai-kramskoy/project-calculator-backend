package org.example.projectcalculator.repository;

import java.util.List;
import java.util.Optional;
import org.example.projectcalculator.model.TeamMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends CrudRepository<TeamMember, Long> {

  List<TeamMember> findAllByProjectId(long projectId);

  Optional<TeamMember> findByIdAndProjectId(long id, long projectId);
}
