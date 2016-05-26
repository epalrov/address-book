/*
 * ContactRestRequestFilter.java - address book CORS filter
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;
 
import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
 
@Provider
@PreMatching
public class ContactRestRequestFilter implements ContainerRequestFilter {
 
    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
        // when HttpMethod comes as OPTIONS, just acknowledge that it accepts.
        if (requestCtx.getRequest().getMethod().equals("OPTIONS")) {
            requestCtx.abortWith(Response.status(Response.Status.OK).build());
        }
    }
}

