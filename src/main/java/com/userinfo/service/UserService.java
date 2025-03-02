package com.userinfo.service;

import com.userinfo.model.User;
import com.userinfo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository UserRepository) {
        this.userRepository = UserRepository;
    }

    @PostConstruct
    private void loadData() {
        userRepository.saveAll(List.of(
                new User("Catherine", "Johnson", "catherine@example.com"),
                new User("Bob", "Smith", "bob@example.com"),
                new User("Charlie", "Williams", "charlie@exmple.com"),
                new User("Ada", "Wong", "ada231@example.com")
        ));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}