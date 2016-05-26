/*
 * ContactLogout.java - address book webservice logout 
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;

/**
 * ContactLogout is a HttpServlet which invalidates the HttpSession.
 */
@WebServlet("/logout")
public class ContactLogout extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
           throws ServletException, IOException {

        request.getSession().invalidate();
        response.sendRedirect("index.html");
    }

}
