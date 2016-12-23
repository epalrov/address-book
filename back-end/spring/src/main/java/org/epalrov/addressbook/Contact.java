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
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Contact entity.
 */
@Entity
@Table(name = "CONTACT")
@NamedQueries({
    /* contacts */
    @NamedQuery(name = "getContact",
                query = "SELECT c FROM Contact c " +
                        "WHERE c.id = :id"),
    @NamedQuery(name = "getContacts",
                query = "SELECT c FROM Contact c " +
                        "ORDER BY c.firstName,c.lastName"),
    @NamedQuery(name = "findContacts",
                query = "SELECT c FROM Contact c " +
                        "WHERE CONCAT(c.firstName, ' ', " +
                        "             c.lastName, ' ', " + 
                        "             c.email) LIKE :key " +
                        "ORDER BY c.firstName,c.lastName"),
    @NamedQuery(name = "countContacts",
                query = "SELECT COUNT(c.id) FROM Contact c"),

    /* contacts by user ID */
    @NamedQuery(name = "getContactByUserId",
                query = "SELECT c FROM Contact c " +
                        "WHERE c.user.id = :userId " +
                        "AND c.id = :contactId"),
    @NamedQuery(name = "getContactsByUserId",
                query = "SELECT c FROM Contact c " +
                        "WHERE c.user.id = :userId " +
                        "ORDER BY c.firstName,c.lastName"),
    @NamedQuery(name = "findContactsByUserId",
                query = "SELECT c FROM Contact c " +
                        "WHERE c.user.id = :userId " +
                        "AND CONCAT(c.firstName, ' ', " +
                        "           c.lastName, ' ', " +
                        "           c.email) LIKE :key " +
                        "ORDER BY c.firstName,c.lastName"),
    @NamedQuery(name = "countContactsiByUserId",
                query = "SELECT COUNT(c.id) FROM Contact c " +
                        "WHERE c.user.id = :userId"),

    /* contacts by username */
    @NamedQuery(name = "getContactByUserName",
                query = "SELECT c FROM Contact c " +
                        "WHERE c.user.username = :userName " +
                        "AND c.id = :contactId"),
    @NamedQuery(name = "getContactsByUserName",
                query = "SELECT c FROM Contact c " +
                        "WHERE c.user.username = :userName " +
                        "ORDER BY c.firstName,c.lastName"),
    @NamedQuery(name = "findContactsByUserName",
                query = "SELECT c FROM Contact c " +
                        "WHERE c.user.username = :userName " +
                        "AND CONCAT(c.firstName, ' ', " +
                        "           c.lastName, ' ', " +
                        "           c.email) LIKE :key " +
                        "ORDER BY c.firstName,c.lastName"),
    @NamedQuery(name = "countContactsiByUserName",
                query = "SELECT COUNT(c.id) FROM Contact c " +
                        "WHERE c.user.username = :user")
})
@XmlRootElement(name = "Contact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "FIRSTNAME")
    @Size(min = 0, max = 50, message = "{contact.firstname}")
    private String firstName;
    @Basic(optional = false)
    @Column(name = "LASTNAME")
    @Size(min = 0, max = 50, message = "{contact.lastname}")
    private String lastName;
    @Basic(optional = false)
    @Column(name = "EMAIL")
    @Size(min = 3, max = 50, message = "{contact.email}")
    @Pattern(regexp = ".+@.+\\.[a-z]+", message = "{contact.email}")
    private String email;
    @ManyToOne(optional = true)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    @XmlTransient
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Contact)) 
            return false;

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
