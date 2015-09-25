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
    serviceName="address-book/soap-api",
    portName = "ContactSoapServicePort",
    targetNamespace = "http://addressbook.epalrov.org")/*,
    endpointInterface = "org.epalrov.addressbook.ContactSoapService")*/
public class ContactSoapService {

    @PersistenceContext
    private EntityManager em;

    @WebMethod(operationName="GetContacts")
    @WebResult(name="contact")
    public List<Contact> getContacts(
/*            @DefaultValue("0") @QueryParam("start") int start,
            @DefaultValue("100") @QueryParam("max") int max*/) {
        Query query = em.createNamedQuery("getContacts")
/*            .setFirstResult(start)
            .setMaxResults(max)*/;
        return (List<Contact>)query.getResultList();
    }

    @WebMethod(operationName="GetContact")
    @WebResult(name="contact")
    public Contact getContact(
            @WebParam(name="id") Integer id) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        return (Contact)query.getSingleResult();
    }

    @WebMethod(operationName="CreateContact")
    public void createContact(
             @WebParam(name="contact") Contact contact) {
        em.persist(contact);
    }

    @WebMethod(operationName="UpdateContact")
    public void updateContact(
             @WebParam(name="id") Integer id,
             @WebParam(name="contact") Contact contact) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.getSingleResult();
        managedContact.setFirstName(contact.getFirstName());
        managedContact.setLastName(contact.getLastName());
        managedContact.setEmail(contact.getEmail());
    }

    @WebMethod(operationName="DeleteContact")
    public void deleteContact(
            @WebParam(name="id") Integer id) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact contact = (Contact)query.getSingleResult();
        em.remove(contact);
    }

}

