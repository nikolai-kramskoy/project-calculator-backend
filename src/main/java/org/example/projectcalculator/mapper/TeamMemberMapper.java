package org.example.projectcalculator.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.example.projectcalculator.dto.TeamMemberDto;
import org.example.projectcalculator.dto.request.CreateUpdateTeamMemberDtoRequest;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.TeamMember;

@Mapper(componentModel = ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TeamMemberMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "request.position", target = "position")
  @Mapping(source = "request.numberOfTeamMembers", target = "numberOfTeamMembers")
  @Mapping(source = "project", target = "project")
  TeamMember toTeam(CreateUpdateTeamMemberDtoRequest request, Project project);

  TeamMemberDto toTeamDto(TeamMember teamMember);
}
