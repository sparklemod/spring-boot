package com.userinfo.service;

import com.userinfo.controller.exception.CustomException;
import com.userinfo.security.CustomUserDetails;
import com.userinfo.model.dto.UserDto;
import com.userinfo.model.Role;
import com.userinfo.model.User;
import com.userinfo.repository.RoleRepository;
import com.userinfo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository UserRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = UserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getPassword(),
                user.getRoles()
        );
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : users) {
            userDtoList.add(getUserDto(user));
        }

        return userDtoList;
    }

    private UserDto getUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setSurname(user.getSurname());
        userDto.setUsername(user.getUsername());
        userDto.setRoleIds(user.getRoleIds());
        userDto.setRoles(user.getRoles().toString());

        return userDto;
    }

    public void addUser(UserDto userDto) {
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "This username already exist");
        }

        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Enter password");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        saveUser(userDto, user);
    }

    public void editUser(UserDto userDto) {
        User existingUser = userRepository
                .findById(userDto.getId())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "User not found"));

        Optional<User> sameUsernameUser = userRepository.findWithSameUsername(userDto.getUsername(), userDto.getId());
        if (sameUsernameUser.isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "This username already exist");
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        saveUser(userDto, existingUser);
    }

    public UserDto getUserDto(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->  new UsernameNotFoundException("User not found"));
        return getUserDto(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void saveUser(UserDto userDto, User user) {
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setUsername(userDto.getUsername());

        if (userDto.getRoleIds().isEmpty()) {
            Role userRole = roleRepository.findByName(Role.USER);
            user.getRoles().add(userRole);
        } else {
            Set<Role> selectedRoles = roleRepository.findByIdIn(userDto.getRoleIds());
            user.setRoles(selectedRoles);
        }

        Long id = userRepository.save(user).getId();

        userDto.setRoles(user.getRoles().toString());
        userDto.setPassword(null);
        userDto.setId(id);
    }
}