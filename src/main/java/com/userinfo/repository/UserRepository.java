package com.userinfo.repository;

import com.userinfo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  UserRepository extends JpaRepository<User, Long> {}