package com.schorsche94.medi_track.service.impl;

import com.schorsche94.medi_track.domain.model.User;
import com.schorsche94.medi_track.domain.repository.MedicationRepository;
import com.schorsche94.medi_track.domain.repository.UserRepository;
import com.schorsche94.medi_track.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public List<User> getUsers() {
        return List.of();
    }

    @Override
    public User createUser(User user) {
        return repository.createUser(user);
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
