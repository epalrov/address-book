/*
 * ContactWebSocketService.java - address book WebSocket service
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.ejb.Stateless;

import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.EncodeException;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
 
import java.io.IOException;

/**
 * ContactWebSocketService is an EJB exposed as WebSocket service
 */
@Stateless
@ServerEndpoint(
    value = "/ws-api/contacts/{operation}",
    encoders = { 
       ContactWebSocketEncoder.class,
       ContactListWebSocketEncoder.class
    },
    decoders = {
       ContactWebSocketDecoder.class
    }
)
public class ContactWebSocketService {

    @PersistenceContext
    private EntityManager em;

    @OnOpen
    public void open(final Session session,
            @PathParam("operation") final String operation) {
    }

    @OnMessage
    public void message(final Session session, final Contact contact,
            @PathParam("operation") final String operation) {

        Query query;
        Object reply;
        Contact managedContact;
        List<Contact> managedContacts;

        switch (operation) {
        case "create":
            em.persist(contact);
            em.flush();
            // reply with the new created object identifier
            reply = contact;
            break;
        case "read":
            if (contact.getId() == 0) {
                query = em.createNamedQuery("getContacts");
                managedContacts = (List<Contact>)query.getResultList();
                // reply with the list of retrieved contact
                reply = managedContacts;
            } else {
                query = em.createNamedQuery("getContact")
                   .setParameter("id", contact.getId());
                managedContact = (Contact)query.getSingleResult();
                // reply with the retrieved contact
                reply = managedContact;
            }
            break;
        case "update":
            query = em.createNamedQuery("getContact")
               .setParameter("id", contact.getId());
            managedContact = (Contact)query.getSingleResult();
            managedContact.setFirstName(contact.getFirstName());
            managedContact.setLastName(contact.getLastName());
            managedContact.setEmail(contact.getEmail());
            // reply with the updated contact
            reply = managedContact;
            break;
        case "delete":
            query = em.createNamedQuery("getContact")
               .setParameter("id", contact.getId());
            managedContact = (Contact)query.getSingleResult();
            em.remove(managedContact);
            // reply with the deleted contact
            reply = managedContact;
            break;
        default:
            return;
        }

        // handle response
        try {
            session.getBasicRemote().sendObject(reply);
        } catch (IOException e) {
        } catch (EncodeException e) {
        } catch (IllegalArgumentException e) {
        }

    }

    @OnClose
    public void close(final Session session,
            @PathParam("operation") final String operation) {
    }

}

