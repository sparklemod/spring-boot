package com.userinfo.service;

import com.userinfo.model.Dto.UserDto;
import com.userinfo.model.Role;
import com.userinfo.model.User;
import com.userinfo.repository.RoleRepository;
import com.userinfo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addUser(UserDto userDto) throws Exception {
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new Exception("This username already existed");
        }

        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new Exception("Enter password");
        }

        saveUser(userDto, new User());
    }

    public void editUser(UserDto userDto) throws Exception {
        User existingUser = userRepository
                .findById(userDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<User> sameUsernameUser = userRepository.findByUsername(userDto.getUsername());
        if (sameUsernameUser.isPresent()) {
            throw new Exception("This username already existed");
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        saveUser(userDto, existingUser);
    }

    public UserDto getUserDto(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->  new UsernameNotFoundException("User not found"));

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setSurname(user.getSurname());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));

        return userDto;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void saveUser(UserDto userDto, User user) {
        Set<Role> selectedRoles = roleRepository.findByIdIn(userDto.getRoleIds());

        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setUsername(userDto.getUsername());

        if (selectedRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(Role.USER);
            user.getRoles().add(userRole);
        } else {
            user.setRoles(selectedRoles);
        }

        userRepository.save(user);
    }
}