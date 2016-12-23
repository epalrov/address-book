/*
 * ContactRestService.java - contact RESTful service
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.net.URI;

/**
 * ContactRestService is a RESTful service
 */
@RestController
@RequestMapping("/contacts")
public class ContactRestService {

    @Autowired
    private ContactDataAccessObjectInterface contactDao;

    @Autowired
    private UserDataAccessObjectInterface userDao;

    private Integer getUserId() {

        Authentication auth = SecurityContextHolder
            .getContext()
            .getAuthentication();

        return userDao.getUserByName(auth.getName()).getId();
    }

    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Contact> getContacts(
            @RequestParam(value = "start", defaultValue = "0") Integer start,
            @RequestParam(value = "max", defaultValue = "100") Integer max,
            @RequestParam(value = "key", defaultValue = "") String key) {

        return contactDao.getContactsByUserId(getUserId(), start, max, key);
    }

    @RequestMapping(
        path = "/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public Contact getContact(
            @PathVariable("id") Integer id) {

        return contactDao.getContactByUserId(getUserId(), id);
    }

    @RequestMapping(
//      path = "/",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> createContact(
            @RequestBody Contact contact) {

        contactDao.createContactByUserId(getUserId(), contact);

        // returns the URI of the new resource in the HTTP header Location field
        URI uri = ServletUriComponentsBuilder.fromCurrentServletMapping()
            .path("/contacts/{id}").build()
            .expand(contact.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @RequestMapping(
        path = "/{id}",
        method = RequestMethod.PUT,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> updateContact(
        @PathVariable("id") Integer id,
        @RequestBody Contact contact) {

        contactDao.updateContactByUserId(getUserId(), id, contact);

        // redirect towards the updated resource
        // URI uri = uriInfo.getAbsoluteRequestMappingBuilder().build();
        // return Response.seeOther(uri).build();
        return ResponseEntity.status(303).build();
    }

    @RequestMapping(
        path = "/{id}",
        method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> deleteContact(
            @PathVariable("id") Integer id) {

        contactDao.deleteContactByUserId(getUserId(), id);

        // ok response with no body
        return ResponseEntity.noContent().build();
    }

}

