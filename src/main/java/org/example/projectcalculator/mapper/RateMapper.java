package org.example.projectcalculator.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.example.projectcalculator.dto.RateDto;
import org.example.projectcalculator.model.Rate;

@Mapper(componentModel = ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RateMapper {

  RateDto toRateDto(Rate rate);
}

