/*
 * ContactRestResponseFilter.java - address book CORS filter
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;
 
import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
 
@Provider
@PreMatching
public class ContactRestResponseFilter implements ContainerResponseFilter {
 
    @Override
    public void filter(ContainerRequestContext requestCtx, ContainerResponseContext responseCtx) throws IOException {
        responseCtx.getHeaders().add("Access-Control-Allow-Origin", "*");
        // responseCtx.getHeaders().add("Access-Control-Allow-Origin", "http://localhost");
        responseCtx.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        responseCtx.getHeaders().add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
    }
}

