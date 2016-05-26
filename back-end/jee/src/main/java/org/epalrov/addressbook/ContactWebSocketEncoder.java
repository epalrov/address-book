/*
 * ContactWebSocketEncoder.java - address book WebSocket message encoder
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import javax.websocket.Encoder;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;

/**
 * ContactWebSocketEncoder (java to json)
 */
public class ContactWebSocketEncoder implements Encoder.Text<Contact> {

    @Override
    public void init(final EndpointConfig config) {
    }
 
    @Override
    public void destroy() {
    }
 
    @Override
    public String encode(final Contact contact) throws EncodeException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("id", contact.getId())
            .add("firstName", contact.getFirstName())
            .add("lastName", contact.getLastName())
            .add("email", contact.getEmail());
        return builder.build().toString();
    }

}

