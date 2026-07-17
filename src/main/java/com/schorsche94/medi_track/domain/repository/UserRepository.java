package com.schorsche94.medi_track.domain.repository;

import com.schorsche94.medi_track.domain.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUser(String id);

    void deleteUser(String id);
}
