package com.userinfo.service;

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

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository UserRepository, RoleRepository roleRepository
            , BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = UserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    @PostConstruct
//    private void loadData() {
//        Role adminRole = new Role();
//        adminRole.setName(Role.ADMIN);
//        roleRepository.save(adminRole);
//
//        Role userRole = new Role();
//        userRole.setName(Role.USER);
//        roleRepository.save(userRole);
//
//        userRepository.saveAll(List.of(
//                new User("admin", "admin", "admin@mail.com", passwordEncoder.encode("admin@mail.com"), Set.of(adminRole)),
//                new User("user", "user", "user@mail.com", passwordEncoder.encode("user@mail.com"), Set.of(userRole)),
//                new User("Catherine", "Johnson", "catherine@mail.com", passwordEncoder.encode("catherine@mail.com"), Set.of(userRole)),
//                new User("Bob", "Smith", "bob@mail.com", passwordEncoder.encode("bob@mail.com"), Set.of(userRole)),
//                new User("Charlie", "Williams", "charlie@mail.com", passwordEncoder.encode("charlie@mail.com"), Set.of(userRole)),
//                new User("Ada", "Wong", "ada231@mail.com", passwordEncoder.encode("ada231@mail.com"), Set.of(userRole))
//        ));
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        if (user.getAuthorities().isEmpty()) {
            Role userRole = roleRepository.findByName(Role.USER);

            user.getRoles().add(userRole);
//            this.setDefaultRole(user);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void setDefaultRole(User user) {
        Role userRole = roleRepository.findByName(Role.USER)
//                .orElseThrow(() -> new RuntimeException("Role " + Role.USER + " not found"))
                ;
        user.setRoles(Set.of(userRole));
    }
}