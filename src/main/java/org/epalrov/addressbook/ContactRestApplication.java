/*
 * ContactRestApplication.java - address book RESTful application
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/* Address book RESTful application endpoint */
@ApplicationPath("rest-api")
public class ContactRestApplication extends Application {
}

