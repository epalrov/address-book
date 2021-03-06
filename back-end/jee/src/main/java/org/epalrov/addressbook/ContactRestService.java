/*
 * ContactRestService.java - address book RESTful webservice
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.net.URI;

/**
 * ContactRestService is an EJB exposed as RESTful webservice
 */
@Stateless
@Path("/contacts")
public class ContactRestService {

    @PersistenceContext
    private EntityManager em;

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext security;

    /* retrieve the current user */
    private Integer getUserId() {

        Query query = em.createNamedQuery("getUserByName")
           .setParameter("userName", security.getUserPrincipal().getName());
        User user = (User)query.getSingleResult();

        return user.getId();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> getContacts(
            @DefaultValue("0") @QueryParam("start") Integer start,
            @DefaultValue("100") @QueryParam("max") Integer max,
            @DefaultValue("") @QueryParam("key") String key) {

        Integer userId = getUserId();
        Query query;

        if (key.length() == 0) {
            query = em.createNamedQuery("getContactsByUserId")
                .setParameter("userId", userId);
        } else {
            query = em.createNamedQuery("findContactsByUserId")
                .setParameter("userId", userId)
                .setParameter("key", "%" + key + "%");
        }
        List<Contact> managedContacts = (List<Contact>)query
            .setFirstResult(start)
            .setMaxResults(max)
            .getResultList();
        return managedContacts;
    }

    @GET
    @Path("{contactId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact getContact(
            @PathParam("contactId") Integer contactId) {

        Integer userId = getUserId();

        Query query = em.createNamedQuery("getContactByUserId")
           .setParameter("userId", userId)
           .setParameter("contactId", contactId);
        Contact managedContact = (Contact)query.getSingleResult();
        return managedContact;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createContact(
        Contact contact) {

        Integer userId = getUserId();

        // set foreign key
        Query query = em.createNamedQuery("getUser")
            .setParameter("id", userId);
        User user = (User)query.getSingleResult();
        contact.setUser(user);

        // contact.Id is generated only after flush!
        em.persist(contact);
        em.flush();

        // returns the URI of the new resource in the HTTP header Location field
        URI uri = uriInfo.getAbsolutePathBuilder()
            .path(contact.getId().toString()).build();
        return Response.created(uri).build();
    }

    @PUT
    @Path("{contactId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateContact(
            @PathParam("contactId") Integer contactId,
            Contact contact) {

        Integer userId = getUserId();

        Query query = em.createNamedQuery("getContactByUserId")
           .setParameter("userId", userId)
           .setParameter("contactId", contactId);
        Contact managedContact = (Contact)query.getSingleResult();
        managedContact.setFirstName(contact.getFirstName());
        managedContact.setLastName(contact.getLastName());
        managedContact.setEmail(contact.getEmail());

        // redirect towards the updated resource
        // URI uri = uriInfo.getAbsolutePathBuilder().build();
        // return Response.seeOther(uri).build();
        return Response.ok().status(303).build();
    }

    @DELETE
    @Path("{contactId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteContact(
            @PathParam("contactId") Integer contactId) {

        Integer userId = getUserId();

        Query query = em.createNamedQuery("getContactByUserId")
           .setParameter("userId", userId)
           .setParameter("contactId", contactId);
        Contact managedContact = (Contact)query.getSingleResult();
        em.remove(managedContact);

        // ok response with no body
        return Response.noContent().build();
    }

}
