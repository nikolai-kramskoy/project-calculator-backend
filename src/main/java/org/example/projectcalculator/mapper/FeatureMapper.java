package org.example.projectcalculator.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.projectcalculator.dto.FeatureDto;
import org.example.projectcalculator.dto.request.CreateFeatureDtoRequest;
import org.example.projectcalculator.dto.request.UpdateFeatureDtoRequest;
import org.example.projectcalculator.model.Feature;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FeatureMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "project", target = "project")
  @Mapping(source = "milestone", target = "milestone")
  @Mapping(source = "request.title", target = "title")
  @Mapping(source = "request.description", target = "description")
  @Mapping(source = "createdAt", target = "createdAt")
  @Mapping(source = "lastUpdatedAt", target = "lastUpdatedAt")
  Feature toFeature(CreateFeatureDtoRequest request, Project project, Milestone milestone,
      LocalDateTime createdAt, LocalDateTime lastUpdatedAt);

  @Mapping(target = "priceInRubles", ignore = true)
  @Mapping(source = "project.id", target = "projectId")
  // There is a null check in MapStruct by default before accessing milestone
  @Mapping(source = "milestone.id", target = "milestoneId")
  FeatureDto toFeatureDto(Feature feature);

  @Mapping(source = "feature.project.id", target = "projectId")
  // There is a null check in MapStruct by default before accessing milestone
  @Mapping(source = "feature.milestone.id", target = "milestoneId")
  FeatureDto toFeatureDto(Feature feature, BigDecimal priceInRubles);

  @Mapping(source = "milestone.id", target = "milestoneId")
  CreateFeatureDtoRequest toCreateFeatureDtoRequest(Feature feature);

  UpdateFeatureDtoRequest toUpdateFeatureDtoRequest(Feature feature, Long newMilestoneId);
}
