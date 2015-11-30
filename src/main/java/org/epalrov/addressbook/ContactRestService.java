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

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> getContacts(
            @DefaultValue("0") @QueryParam("start") Integer start,
            @DefaultValue("100") @QueryParam("max") Integer max,
            @DefaultValue("") @QueryParam("key") String key) {
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

    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact getContact(@PathParam("id") Integer id) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.getSingleResult();
        return managedContact;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createContact(Contact contact) {
        em.persist(contact);
        // contact.Id is generated only after flush!
        em.flush();
        // returns the URI of the new resource in the HTTP header Location field
        URI uri = uriInfo.getAbsolutePathBuilder()
            .path(contact.getId().toString()).build();
        return Response.created(uri).build();
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
        // redirect towards the updated resource
        // URI uri = uriInfo.getAbsolutePathBuilder().build();
        // return Response.seeOther(uri).build();
        return Response.ok().status(303).build();
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteContact(@PathParam("id") Integer id) {
        Query query = em.createNamedQuery("getContact")
           .setParameter("id", id);
        Contact managedContact = (Contact)query.getSingleResult();
        em.remove(managedContact);
        // ok response with no body
        return Response.noContent().build();
    }

}
