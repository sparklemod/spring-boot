package com.userinfo.loader;

import com.userinfo.model.Role;
import com.userinfo.model.User;
import com.userinfo.repository.RoleRepository;
import com.userinfo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataLoader {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void loadData() {
        Role adminRole = new Role();
        adminRole.setName(Role.ADMIN);
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName(Role.USER);
        roleRepository.save(userRole);

        userRepository.saveAll(List.of(
                new User("admin", "admin", "admin@test.com", passwordEncoder.encode("admin@test.com"), Set.of(adminRole, userRole)),
                new User("user", "user", "user@test.com", passwordEncoder.encode("user@test.com"), Set.of(userRole)),
                new User("Catherine", "Johnson", "catherine@mail.com", passwordEncoder.encode("catherine@mail.com"), Set.of(userRole)),
                new User("Bob", "Smith", "bob@mail.com", passwordEncoder.encode("bob@mail.com"), Set.of(userRole)),
                new User("Charlie", "Williams", "charlie@mail.com", passwordEncoder.encode("charlie@mail.com"), Set.of(userRole)),
                new User("Ada", "Wong", "ada231@mail.com", passwordEncoder.encode("ada231@mail.com"), Set.of(userRole))
        ));
    }
}