package org.example.projectcalculator.mapper;

import org.example.projectcalculator.dto.TeamMemberDto;
import org.example.projectcalculator.dto.request.CreateUpdateTeamMemberDtoRequest;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.TeamMember;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TeamMemberMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "request.position", target = "position")
  @Mapping(source = "request.numberOfTeamMembers", target = "numberOfTeamMembers")
  @Mapping(source = "project", target = "project")
  TeamMember toTeamMember(CreateUpdateTeamMemberDtoRequest request, Project project);

  TeamMemberDto toTeamMemberDto(TeamMember teamMember);

  CreateUpdateTeamMemberDtoRequest toCreateUpdateTeamMemberDtoRequest(TeamMember teamMember);
}
