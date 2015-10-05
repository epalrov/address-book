/*
 * ContactWebSocketService.java - address book WebSocket service
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.ejb.Stateless;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
 
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

/**
 * ContactWebSocketService is an EJB exposed as WebSocket service
 */
@Stateless
@ServerEndpoint(
    value = "/ws-api/contacts/{operation}",
    encoders = ContactWebSocketEncoder.class,
    decoders = ContactWebSocketDecoder.class)
public class ContactWebSocketService {

    @PersistenceContext
    private EntityManager em;

    private final Logger log = Logger.getLogger(getClass().getName());
 
    @OnOpen
    public void open(final Session session,
            @PathParam("operation") final String operation) {
        log.info("on-open: " + operation);
    }

    @OnMessage
    public void message(final Session session, final Contact contact,
            @PathParam("operation") final String operation) {
        log.info("on-message: " + operation);

        Query query;
        Object reply;
        Contact managedContact;

        switch (operation) {
        case "create":
            em.persist(contact);
            em.flush();
            // respond with the new created object identifier
            reply = contact.getId();
            break;
        case "read":
            query = em.createNamedQuery("getContact")
               .setParameter("id", contact.getId());
            managedContact = (Contact)query.getSingleResult();
            // respond with the found contact
            reply = managedContact;
            break;
        case "update":
            query = em.createNamedQuery("getContact")
               .setParameter("id", contact.getId());
            managedContact = (Contact)query.getSingleResult();
            managedContact.setFirstName(contact.getFirstName());
            managedContact.setLastName(contact.getLastName());
            managedContact.setEmail(contact.getEmail());
            // respond with the updated contact
            reply = managedContact;
            break;
        case "delete":
            query = em.createNamedQuery("getContact")
               .setParameter("id", contact.getId());
            managedContact = (Contact)query.getSingleResult();
            em.remove(managedContact);
            reply = null;
            break;
        default:
            log.info("invalid operation!");
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
        log.info("on-colse: " + operation);
    }

}

