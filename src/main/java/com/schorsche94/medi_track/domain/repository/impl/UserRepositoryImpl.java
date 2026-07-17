package com.schorsche94.medi_track.domain.repository.impl;

import com.schorsche94.medi_track.domain.model.User;
import com.schorsche94.medi_track.domain.repository.UserRepository;
import com.schorsche94.medi_track.domain.repository.jpa.MedicationJpaRepository;
import com.schorsche94.medi_track.domain.repository.jpa.UserJpaRepository;
import com.schorsche94.medi_track.domain.repository.mapper.MedicationMapper;
import com.schorsche94.medi_track.domain.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserJpaRepository repository;

    @Autowired
    private UserMapper mapper;

    @Override
    public List<User> getUsers() {
        return List.of();
    }

    @Override
    public User createUser(User user) {
        var entity = mapper.toEntity(user);
        entity = repository.save(entity);
        return mapper.toModel(entity);
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUser(String id) {
        return null;
    }

    @Override
    public void deleteUser(String id) {

    }
}
