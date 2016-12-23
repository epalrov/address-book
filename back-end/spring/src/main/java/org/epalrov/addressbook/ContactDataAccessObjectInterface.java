/*
 * ContactDataAccessObjectInterface.java - contact DAO interface
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import java.util.List;

/**
 * ContactDataAccessObjectInterface
 */
public interface ContactDataAccessObjectInterface {

    /* contacts */
    public List<Contact> getContacts(Integer start, Integer max, String key);
    public Contact getContact(Integer id);
    public void createContact(Contact contact);
    public void updateContact(Integer id, Contact contact);
    public void deleteContact(Integer id);

    /* contacts by user */
    public List<Contact> getContactsByUserId(Integer userId, Integer start, Integer max, String key);
    public Contact getContactByUserId(Integer userId, Integer contactId);
    public void createContactByUserId(Integer userId, Contact contact);
    public void updateContactByUserId(Integer userId, Integer contactId, Contact contact);
    public void deleteContactByUserId(Integer userId, Integer contactId);

}

