package com.userinfo.service;

import com.userinfo.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class DataInitializationService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void initializeData() {
        if (entityManager.createQuery("SELECT COUNT(u) FROM User u").getSingleResult().equals(0L)) {
            entityManager.persist(new User("Catherine", "Johnson", "catherine@example.com"));
            entityManager.persist(new User("Bob", "Smith", "bob@example.com"));
            entityManager.persist(new User("Charlie", "Williams", "charlie@example.com"));
            entityManager.flush();
        }
    }
}