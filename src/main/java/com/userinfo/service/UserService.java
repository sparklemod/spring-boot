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

    public void saveUser(UserDto userDto) {
        Set<Role> selectedRoles = roleRepository.findByIdIn(userDto.getRoleIds());
        User existingUser = userRepository.findByUsername(userDto.getUsername()).orElseGet(User::new);

        existingUser.setName(userDto.getName());
        existingUser.setSurname(userDto.getSurname());
        existingUser.setUsername(userDto.getUsername());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (selectedRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(Role.USER);
            existingUser.getRoles().add(userRole);
        } else {
            existingUser.setRoles(selectedRoles);
        }

        userRepository.save(existingUser);
    }

    public UserDto getUserDto(Long id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
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
}