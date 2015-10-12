/*
 * ContactListWebSocketEncoder.java - address book WebSocket message encoder
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import javax.websocket.Encoder;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;

import java.util.List;

/**
 * ContactListWebSocketEncoder (java to json)
 */
public class ContactListWebSocketEncoder implements Encoder.Text<List<Contact>> {

    @Override
    public void init(final EndpointConfig config) {
    }
 
    @Override
    public void destroy() {
    }
 
    @Override
    public String encode(final List<Contact> contacts) throws EncodeException {

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Contact contact : contacts) {
            builder.add(Json.createObjectBuilder()
                .add("id", contact.getId())
                .add("firstName", contact.getFirstName())
                .add("lastName", contact.getLastName())
                .add("email", contact.getEmail())
            );
        }
        return builder.build().toString();
    }

}

