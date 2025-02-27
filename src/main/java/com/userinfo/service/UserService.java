package com.userinfo.service;

import com.userinfo.model.User;
import com.userinfo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createOrUpdateUser(User user) {
        if (user.getId() == null) {
            userRepository.create(user);
        } else {
            userRepository.update(user);
        }
    }

    public User getUser(Long id) {
        return userRepository.findById(id);
    }

    public boolean deleteUser(Long id) {
        return userRepository.delete(id);
    }
}