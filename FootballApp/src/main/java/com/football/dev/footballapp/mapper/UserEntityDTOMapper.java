package com.football.dev.footballapp.mapper;

import com.football.dev.footballapp.dto.UserEntityDto;
import com.football.dev.footballapp.models.UserEntity;

import java.util.function.Function;

public class UserEntityDTOMapper implements Function<UserEntity, UserEntityDto> {
    @Override
    public UserEntityDto apply(UserEntity userEntity) {
        return new UserEntityDto(userEntity.getId(),userEntity.getFirstName(),userEntity.getLastName(),userEntity.getEmail(), userEntity.getPhone(), userEntity.getCountry(),userEntity.getBirthDate(),userEntity.getRole().getDescription(),userEntity.getProfile_picture(),userEntity.getAddress(),userEntity.getCity(),userEntity.getPostal_code());
    }
}