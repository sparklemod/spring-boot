package com.userinfo.repository;

import com.userinfo.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findAll() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

    @Transactional
    public void create(User user) {
        entityManager.persist(user);
        entityManager.flush();
    }

    @Transactional
    public void update(User user) {
        entityManager.merge(user);
        entityManager.flush();
    }

    public User findById(long id) {
        return entityManager.find(User.class, id);
    }

    @Transactional
    public boolean delete(long id) throws NullPointerException {
        User user = findById(id);
        if (user == null) {
            return false;
        }

        entityManager.remove(user);
        entityManager.flush();
        return true;
    }
}