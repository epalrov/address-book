/*
 * UserRestService.java - address book RESTful webservice
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
import javax.persistence.NoResultException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.net.URI;

/**
 * UserRestService is an EJB exposed as RESTful webservice
 */
@Stateless
@Path("/users")
public class UserRestService {

    @PersistenceContext
    private EntityManager em;

    @Context
    private UriInfo uriInfo;

    @Context 
    private SecurityContext security;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers(
            @DefaultValue("0") @QueryParam("start") Integer start,
            @DefaultValue("100") @QueryParam("max") Integer max,
            @DefaultValue("") @QueryParam("key") String key) {

        Query query;
        if (key.length() == 0) {
            query = em.createNamedQuery("getUsers");
        } else {
            query = em.createNamedQuery("findUsers")
                .setParameter("key", "%" + key + "%");
        }
        List<User> managedUsers = (List<User>)query
            .setFirstResult(start)
            .setMaxResults(max)
            .getResultList();
        return managedUsers;
    }

    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(
            @PathParam("id") Integer id) {

        Query query = em.createNamedQuery("getUser")
           .setParameter("id", id);
        User managedUser = (User)query.getSingleResult();
        return managedUser;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(
            User user) {

        // user.Id is generated only after flush!
        em.persist(user);
        em.flush();

        // returns the URI of the new resource in the HTTP header Location field
        URI uri = uriInfo.getAbsolutePathBuilder()
            .path(user.getUsername().toString()).build();
        return Response.created(uri).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @PathParam("id") Integer id, User user) {

        Query query = em.createNamedQuery("getUser")
           .setParameter("id", id);
        User managedUser = (User)query.getSingleResult();
        managedUser.setPassword(user.getPassword());

        // redirect towards the updated resource
        // URI uri = uriInfo.getAbsolutePathBuilder().build();
        // return Response.seeOther(uri).build();
        return Response.ok().status(303).build();
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(
            @PathParam("id") Integer id) {

        Query query = em.createNamedQuery("getUser")
           .setParameter("id", id);
        User managedUser = (User)query.getSingleResult();
        em.remove(managedUser);

        // ok response with no body
        return Response.noContent().build();
    }

    @GET
    @Path("{userId}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> getContacts(
            @PathParam("userId") Integer userId,
            @DefaultValue("0") @QueryParam("start") Integer start,
            @DefaultValue("100") @QueryParam("max") Integer max,
            @DefaultValue("") @QueryParam("key") String key) {

        if (security.isUserInRole("EndUser") || 
            security.isUserInRole("AdminUser")) {
//          throw new ForbiddenException();
        }
//    Response response = Response
//        .status(Response.Status.FORBIDDEN)
//        .entity("Paolo e Valeria")
//        .build();
//    throw new WebApplicationException(response);

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
    @Path("{userId}/contacts/{contactId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact getContact(
            @PathParam("userId") Integer userId,
            @PathParam("contactId") Integer contactId) {

        Query query = em.createNamedQuery("getContactByUserId")
           .setParameter("userId", userId)
           .setParameter("contactId", contactId);
        Contact managedContact = (Contact)query.getSingleResult();
        return managedContact;
    }

    @POST
    @Path("{userId}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createContact(
            @PathParam("userId") Integer userId,
            Contact contact) {

        // set foreign key
        Query query = em.createNamedQuery("getUserById")
            .setParameter("userId", userId);
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
    @Path("{userId}/contacts/{contactId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateContact(
            @PathParam("userId") Integer userId,
            @PathParam("contactId") Integer contactId,
            Contact contact) {

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
    @Path("{userId}/contacts/{contactId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteContact(
            @PathParam("userId") Integer userId,
            @PathParam("contactId") Integer contactId,
            Contact contact) {

        Query query = em.createNamedQuery("getContactByUserId")
           .setParameter("userId", userId)
           .setParameter("contactId", contactId);
        Contact managedContact = (Contact)query.getSingleResult();
        em.remove(managedContact);

        // ok response with no body
        return Response.noContent().build();
    }

}
