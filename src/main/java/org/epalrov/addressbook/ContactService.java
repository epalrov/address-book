/*
 * ContactService.java - address book restful webservice
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.ejb.Stateless;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.net.URI;
import java.util.List;

/**
 * ContactService is an EJB exposed as RESTful service
 */
@Stateless
@Path("/contacts")
public class ContactService {

    @PersistenceContext
    private EntityManager em;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> getContacts(
            @DefaultValue("0") @QueryParam("start") int start,
            @DefaultValue("100") @QueryParam("max") int max) {
        Query query = em.createNamedQuery("getContacts")
            .setFirstResult(start)
            .setMaxResults(max);
        return (List<Contact>)query.getResultList();
    }

    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact getContact(@PathParam("id") Integer id) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        return (Contact)query.getSingleResult();
        //return em.find(Contact.class, id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createContact(Contact contact) {
        em.persist(contact);
        return Response.created(URI.create("/" + contact.getId())).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateContact(@PathParam("id") Integer id, Contact contact) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.getSingleResult();
        managedContact.setFirstName(contact.getFirstName());
        managedContact.setLastName(contact.getLastName());
        managedContact.setEmail(contact.getEmail());
        return Response.ok().status(303).build(); //return a seeOther code
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteContact(@PathParam("id") Integer id) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact contact = (Contact)query.getSingleResult();
        em.remove(contact);
    }

}
