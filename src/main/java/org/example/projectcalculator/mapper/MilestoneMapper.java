package org.example.projectcalculator.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.request.CreateUpdateMilestoneDtoRequest;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Project;

@Mapper(componentModel = ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MilestoneMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "project", target = "project")
  @Mapping(source = "request.title", target = "title")
  @Mapping(source = "request.description", target = "description")
  @Mapping(source = "estimateInDays", target = "estimateInDays")
  @Mapping(source = "createdAt", target = "createdAt")
  @Mapping(source = "lastUpdatedAt", target = "lastUpdatedAt")
  Milestone toMilestone(CreateUpdateMilestoneDtoRequest request, Project project,
      LocalDateTime createdAt, LocalDateTime lastUpdatedAt, BigDecimal estimateInDays);

  @Mapping(source = "project.id", target = "projectId")
  @Mapping(target = "estimateInDays", ignore = true)
  @Mapping(target = "priceInRubles", ignore = true)
  MilestoneDto toMilestoneDto(Milestone milestone);

  @Mapping(source = "milestone.project.id", target = "projectId")
  MilestoneDto toMilestoneDto(Milestone milestone, BigDecimal priceInRubles);

  CreateUpdateMilestoneDtoRequest toCreateUpdateMilestoneDtoRequest(Milestone milestone);
}
