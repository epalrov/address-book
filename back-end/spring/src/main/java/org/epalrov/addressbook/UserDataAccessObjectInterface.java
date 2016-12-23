/*
 * UserDataAccessObjectInterface.java - user DAO interface
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import java.util.List;

/**
 * UserDataAccessObjectInterface
 */
public interface UserDataAccessObjectInterface {

    /* users */
    public List<User> getUsers(Integer start, Integer max, String key);
    public User getUser(Integer id);
    public void createUser(User user);
    public void updateUser(Integer id, User user);
    public void deleteUser(Integer id);

    /* user by ID/Name */
    public User getUserById(Integer userId);
    public User getUserByName(String userName);
}

