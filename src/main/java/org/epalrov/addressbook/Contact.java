/*
 * Contact.java - address book contact entity
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contact entity.
 */
@Entity
@Table(name = "CONTACT")
@NamedQueries({
    @NamedQuery(name = "getContact",
                query = "SELECT c FROM Contact c WHERE c.id = :id"),
    @NamedQuery(name = "getContacts",
                query = "SELECT c FROM Contact c ORDER BY c.id"),
    @NamedQuery(name = "countContacts",
                query = "SELECT COUNT(c.id) FROM Contact c")
})
@XmlRootElement
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "FIRSTNAME")
    @Size(min=3, max=50, message="{contact.firstname}")
    private String firstName;
    @Basic(optional = false)
    @Column(name = "LASTNAME")
    @Size(min=3, max=50, message="{contact.lastname}")
    private String lastName;
    @Basic(optional = false)
    @Column(name = "EMAIL")
    @Size(min=3, max=50, message= "{contact.email}")
    @Pattern(regexp = ".+@.+\\.[a-z]+", message= "{contact.email}")
    private String email;

    public Contact() {
    }

    public Contact(Integer id, String firstName, String lastName,
            String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        // same reference
        if (o == this)
            return true;
        // check instance type 
        if (!(o instanceof Contact)) 
            return false;

        // safe cast and comparison
        Contact other = (Contact)o;
        if ((this.id == null && other.id != null) ||
                (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.id == null ? 0 : this.id.hashCode();
    }

    @Override
    public String toString() {
        return "org.epalrov.addressbook.Contact[ id=" + this.id + " ]";
    }

}
