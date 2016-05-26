/*
 * ContactDataAccessObjectInterface.java - address book DAO Interface
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import org.epalrov.addressbook.Contact;

import java.util.List;

/**
 * ContactDataAccessObjectInterface
 */
public interface ContactDataAccessObjectInterface {

    public List<Contact> getContacts(Integer start, Integer max, String key);
    public Contact getContact(Integer id);
    public void createContact(Contact contact);
    public void updateContact(Integer id, Contact contact);
    public void deleteContact(Integer id);
}

