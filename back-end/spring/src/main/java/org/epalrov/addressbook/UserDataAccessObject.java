/*
 * UserDataAccessObject.java - user DAO implementation
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserDataAccessObject is a data access object (DAO) that provides an
 * abstract interface to the database or to the persistence mechanism.
 * By mapping application calls to the persistence layer, the DAO provides
 * some specific data operations without exposing details of the database.
 * This isolation supports the Single responsibility principle.
 * It separates what data access the application needs, in terms of
 * domain-specific objects and data types (the public interface of the DAO).
 */
@Repository
public class UserDataAccessObject implements UserDataAccessObjectInterface {

    @Autowired
    private SessionFactory sf;

    public UserDataAccessObject() {
    }

    public UserDataAccessObject(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    @Transactional
    public List<User> getUsers(Integer start, Integer max, String key) {
        Query query;
        if (key.length() == 0) {
            query = sf.getCurrentSession().getNamedQuery("getUsers");
        } else {
            query = sf.getCurrentSession().getNamedQuery("findUsers")
                .setParameter("key", "%" + key + "%");
        }
        List<User> managedUsers = (List<User>)query
            .setFirstResult(start)
            .setMaxResults(max)
            .list();
        return managedUsers;
    }

    @Override
    @Transactional
    public User getUser(Integer id) {
        Query query = sf.getCurrentSession().getNamedQuery("getUser")
           .setParameter("id", id);
        User managedUser = (User)query.uniqueResult();
        return managedUser;
    }

    @Override
    @Transactional
    public void createUser(User user) {
        // user.Id is generated only after flush!
        sf.getCurrentSession().persist(user);
        sf.getCurrentSession().flush();
    }

    @Override
    @Transactional
    public void updateUser(Integer id, User user) {
        Query query = sf.getCurrentSession().getNamedQuery("getUser")
           .setParameter("id", id);
        User managedUser = (User)query.uniqueResult();
        managedUser.setUsername(user.getUsername());
        managedUser.setPassword(user.getPassword());
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        Query query = sf.getCurrentSession().getNamedQuery("getUser")
           .setParameter("id", id);
        User managedUser = (User)query.uniqueResult();
        sf.getCurrentSession().delete(managedUser);
    }

    @Override
    @Transactional
    public User getUserById(Integer userId) {
        Query query = sf.getCurrentSession().getNamedQuery("getUserById")
           .setParameter("userId", userId);
        User managedUser = (User)query.uniqueResult();
        return managedUser;
    }

    @Override
    @Transactional
    public User getUserByName(String userName) {
        Query query = sf.getCurrentSession().getNamedQuery("getUserByName")
           .setParameter("userName", userName);
        User managedUser = (User)query.uniqueResult();
        return managedUser;
    }

}
