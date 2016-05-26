/*
 * ContactRestService.java - address book RESTful webservice
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import org.epalrov.addressbook.Contact;
import org.epalrov.addressbook.ContactDataAccessObjectInterface;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.LinkedList;
import java.net.URI;

/**
 * ContactRestService is an EJB exposed as RESTful webservice
 */
@RestController
@RequestMapping("/contacts")
public class ContactRestService {

    @Autowired
    private ContactDataAccessObjectInterface dao;

    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Contact> getContacts(
            @RequestParam(value = "start", defaultValue = "0") Integer start,
            @RequestParam(value = "max", defaultValue = "100") Integer max,
            @RequestParam(value = "key", defaultValue = "") String key) {
        return dao.getContacts(start, max, key);
    }

    @RequestMapping(
        path = "/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public Contact getContact(@PathVariable("id") Integer id) {
        return dao.getContact(id);
    }

    @RequestMapping(
//        path = "/",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> createContact(@RequestBody Contact contact) {
        dao.createContact(contact);
        // returns the URI of the new resource in the HTTP header Location field
        URI uri = ServletUriComponentsBuilder.fromCurrentServletMapping()
            .path("/contacts/{id}").build()
            .expand(contact.getId()).toUri();
        //
        //URI uri = uriInfo.getAbsoluteRequestMappingBuilder()
        //    .path(contact.getId().toString()).build();
        //return ResponseEntity.created(uri).build();
        return ResponseEntity.created(uri).build();
    }

    @RequestMapping(
        path = "/{id}",
        method = RequestMethod.PUT,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> updateContact(@PathVariable("id") Integer id, @RequestBody Contact contact) {
        dao.updateContact(id, contact);
        // redirect towards the updated resource
        // URI uri = uriInfo.getAbsoluteRequestMappingBuilder().build();
        // return Response.seeOther(uri).build();
        return ResponseEntity.status(303).build();
    }

    @RequestMapping(
        path = "/{id}",
        method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> deleteContact(@PathVariable("id") Integer id) {
        dao.deleteContact(id);
        // ok response with no body
        return ResponseEntity.noContent().build();
    }

}

