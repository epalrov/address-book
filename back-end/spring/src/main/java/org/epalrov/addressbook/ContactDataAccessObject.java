/*
 * ContactDataAccessObject.java - address book DAO Implementation
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import org.epalrov.addressbook.Contact;

import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ContactDataAccessObject is a Spring Bean
 */
@Repository
public class ContactDataAccessObject implements ContactDataAccessObjectInterface {

    @Autowired
    private SessionFactory sf;

    public ContactDataAccessObject() {
    }

    public ContactDataAccessObject(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    @Transactional
    public List<Contact> getContacts(Integer start, Integer max, String key) {
        Query query;
        if (key.length() == 0) {
            query = sf.getCurrentSession().getNamedQuery("getContacts");
        } else {
            query = sf.getCurrentSession().getNamedQuery("findContacts")
                .setParameter("key", "%" + key + "%");
        }
        List<Contact> managedContacts = (List<Contact>)query
            .setFirstResult(start)
            .setMaxResults(max)
            .list();
        return managedContacts;
    }

    @Override
    @Transactional
    public Contact getContact(Integer id) {
        Query query = sf.getCurrentSession().getNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.uniqueResult();
        return managedContact;
    }

    @Override
    @Transactional
    public void createContact(Contact contact) {
        sf.getCurrentSession().persist(contact);
        // contact.Id is generated only after flush!
        sf.getCurrentSession().flush();
    }

    @Override
    @Transactional
    public void updateContact(Integer id, Contact contact) {
        Query query = sf.getCurrentSession().getNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.uniqueResult();
        managedContact.setFirstName(contact.getFirstName());
        managedContact.setLastName(contact.getLastName());
        managedContact.setEmail(contact.getEmail());
    }

    @Override
    @Transactional
    public void deleteContact(Integer id) {
        Query query = sf.getCurrentSession().getNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.uniqueResult();
        sf.getCurrentSession().delete(managedContact);
    }

}
