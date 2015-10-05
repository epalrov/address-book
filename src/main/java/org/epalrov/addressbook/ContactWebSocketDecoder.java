/*
 * ContactWebSocketDecoder.java - address book WebSocket message decoder
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.json.Json;
import javax.json.JsonObject;

import javax.websocket.Decoder;
import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;

import java.util.List;
import java.io.StringReader;

/**
 * ContactWebSocketDecoder (json to java)
 */
public class ContactWebSocketDecoder implements Decoder.Text<Contact> {

    @Override
    public void init(final EndpointConfig config) {
    }
 
    @Override
    public void destroy() {
    }
 
    @Override
    public Contact decode(final String text) throws DecodeException {
        JsonObject obj = Json.createReader(new StringReader(text)).readObject();
        Contact contact = new Contact();
        // try {
        if (!obj.isNull("id"))
            contact.setId(obj.getInt("id"));
        if (!obj.isNull("firstName"))
            contact.setFirstName(obj.getString("firstName"));
        if (!obj.isNull("lastName"))
            contact.setLastName(obj.getString("lastName"));
        if (!obj.isNull("email"))
            contact.setEmail(obj.getString("email"));
        // } catch (java.lang.NullPointerException e) {
        // }
        return contact;
    }

    @Override
    public boolean willDecode(final String s) {
        return true;
    }

}

