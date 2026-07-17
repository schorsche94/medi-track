package com.schorsche94.medi_track.domain.repository.mapper;

import com.schorsche94.medi_track.domain.entity.MedicationEntity;
import com.schorsche94.medi_track.domain.entity.UserEntity;
import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(User model);

    User toModel(UserEntity entity);
}
