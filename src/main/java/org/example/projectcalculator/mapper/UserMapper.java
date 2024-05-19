package org.example.projectcalculator.mapper;

import java.time.LocalDateTime;
import org.example.projectcalculator.dto.UserDto;
import org.example.projectcalculator.dto.request.CreateUserDtoRequest;
import org.example.projectcalculator.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "passwordHash", target = "passwordHash")
  @Mapping(source = "createdAt", target = "createdAt")
  @Mapping(source = "lastUpdatedAt", target = "lastUpdatedAt")
  User toUser(CreateUserDtoRequest request, String passwordHash, LocalDateTime createdAt,
      LocalDateTime lastUpdatedAt);

  UserDto toUserDto(User user);

  @Mapping(source = "password", target = "password")
  CreateUserDtoRequest toCreateUserDtoRequest(User user, String password);
}
