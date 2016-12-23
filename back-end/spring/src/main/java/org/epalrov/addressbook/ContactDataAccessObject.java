/*
 * ContactDataAccessObject.java - contact DAO implementation
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
 * ContactDataAccessObject is a data access object (DAO) that provides an
 * abstract interface to the database or to the persistence mechanism.
 * By mapping application calls to the persistence layer, the DAO provides
 * some specific data operations without exposing details of the database.
 * This isolation supports the Single responsibility principle.
 * It separates what data access the application needs, in terms of
 * domain-specific objects and data types (the public interface of the DAO).
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
        // contact.Id is generated only after flush!
        sf.getCurrentSession().persist(contact);
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

    @Override
    @Transactional
    public List<Contact> getContactsByUserId(Integer userId, Integer start, Integer max, String key) {

        Query query;
        if (key.length() == 0) {
            query = sf.getCurrentSession().getNamedQuery("getContactsByUserId")
                .setParameter("userId", userId);
        } else {
            query = sf.getCurrentSession().getNamedQuery("findContactsByUserId")
                .setParameter("userId", userId)
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
    public Contact getContactByUserId(Integer userId, Integer contactId) {

        Query query = sf.getCurrentSession().getNamedQuery("getContactByUserId")
            .setParameter("userId", userId)
            .setParameter("contactId", contactId);
        Contact managedContact = (Contact)query.uniqueResult();
        return managedContact;
    }

    @Override
    @Transactional
    public void createContactByUserId(Integer userId, Contact contact) {

        // set the foreign key user.Id
        Query query = sf.getCurrentSession().getNamedQuery("getUserById")
            .setParameter("userId", userId);
        User managedUser = (User)query.uniqueResult();
        contact.setUser(managedUser);

        // contact.Id is generated only after flush!
        sf.getCurrentSession().persist(contact);
        sf.getCurrentSession().flush();
    }

    @Override
    @Transactional
    public void updateContactByUserId(Integer userId, Integer contactId, Contact contact) {
        Query query = sf.getCurrentSession().getNamedQuery("getContactByUserId")
            .setParameter("userId", userId)
            .setParameter("contactId", contactId);
        Contact managedContact = (Contact)query.uniqueResult();
        managedContact.setFirstName(contact.getFirstName());
        managedContact.setLastName(contact.getLastName());
        managedContact.setEmail(contact.getEmail());
    }

    @Override
    @Transactional
    public void deleteContactByUserId(Integer userId, Integer contactId) {
        Query query = sf.getCurrentSession().getNamedQuery("getContactByUserId")
            .setParameter("userId", userId)
            .setParameter("contactId", contactId);
        Contact managedContact = (Contact)query.uniqueResult();
        sf.getCurrentSession().delete(managedContact);
    }

}
