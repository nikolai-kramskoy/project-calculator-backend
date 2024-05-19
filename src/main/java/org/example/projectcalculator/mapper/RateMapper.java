package org.example.projectcalculator.mapper;

import org.example.projectcalculator.dto.RateDto;
import org.example.projectcalculator.dto.request.UpdateRateDtoRequest;
import org.example.projectcalculator.model.Rate;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RateMapper {

  RateDto toRateDto(Rate rate);

  UpdateRateDtoRequest toUpdateRateDtoRequest(Rate rate);
}

