package org.example.projectcalculator.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.projectcalculator.dto.ProjectDto;
import org.example.projectcalculator.dto.request.CreateUpdateProjectDtoRequest;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProjectMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "creator", target = "creator")
  @Mapping(source = "estimateInDays", target = "estimateInDays")
  @Mapping(source = "createdAt", target = "createdAt")
  @Mapping(source = "lastUpdatedAt", target = "lastUpdatedAt")
  @Mapping(target = "teamMembers", ignore = true)
  @Mapping(target = "rates", ignore = true)
  Project toProject(CreateUpdateProjectDtoRequest request, User creator, BigDecimal estimateInDays,
      LocalDateTime createdAt, LocalDateTime lastUpdatedAt);

  @Mapping(source = "creator.id", target = "creatorId")
  @Mapping(target = "estimateInDays", ignore = true)
  @Mapping(target = "priceInRubles", ignore = true)
  ProjectDto toProjectDto(Project project);

  @Mapping(source = "project.creator.id", target = "creatorId")
  ProjectDto toProjectDto(Project project, BigDecimal priceInRubles);

  CreateUpdateProjectDtoRequest toCreateProjectDtoRequest(Project project);
}
