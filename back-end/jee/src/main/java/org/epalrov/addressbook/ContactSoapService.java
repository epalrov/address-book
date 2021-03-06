/*
 * ContactSoapService.java - address book SOAP webservice
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.ejb.Stateless;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebParam;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

/**
 * ContactSoapService is an EJB exposed as SOAP webservice
 */
@Stateless
@WebService(
    serviceName="address-book-jee/soap-api",
    portName = "ContactSoapServicePort",
    targetNamespace = "http://addressbook.epalrov.org")
    // endpointInterface = "org.epalrov.addressbook.ContactSoapService"
public class ContactSoapService {

    @PersistenceContext
    private EntityManager em;

    @WebMethod(operationName="GetContacts")
    @WebResult(name="contact")
    public List<Contact> getContacts(
            @WebParam(name="start") Integer start,
            @WebParam(name="max") Integer max,
            @WebParam(name="key") String key) {
        Query query;
        if (key.length() == 0) {
            query = em.createNamedQuery("getContacts");
        } else {
            query = em.createNamedQuery("findContacts")
                .setParameter("key", "%" + key + "%");
        }
        List<Contact> managedContacts = (List<Contact>)query
            .setFirstResult(start)
            .setMaxResults(max)
            .getResultList();
        return managedContacts;
    }

    @WebMethod(operationName="GetContact")
    @WebResult(name="contact")
    public Contact getContact(
            @WebParam(name="id") Integer id) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.getSingleResult();
        return managedContact;
    }

    @WebMethod(operationName="CreateContact")
    @WebResult(name="id")
    public Integer createContact(
            @WebParam(name="contact") Contact contact) {
        em.persist(contact);
        em.flush();
        return contact.getId();
    }

    @WebMethod(operationName="UpdateContact")
    @WebResult(name="contact")
    public Contact updateContact(
            @WebParam(name="id") Integer id,
            @WebParam(name="contact") Contact contact) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.getSingleResult();
        managedContact.setFirstName(contact.getFirstName());
        managedContact.setLastName(contact.getLastName());
        managedContact.setEmail(contact.getEmail());
        return managedContact;
    }

    @WebMethod(operationName="DeleteContact")
    public void deleteContact(
            @WebParam(name="id") Integer id) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.getSingleResult();
        em.remove(managedContact);
    }

}

