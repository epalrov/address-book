/*
 * User.java - address book user entity
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;

/* fixes infinite recursion in bidirectional relationships and lazy loadingi */
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User entity.
 */
@Entity
@Table(name = "\"USER\"")
@NamedQueries({
    /* users */
    @NamedQuery(name = "getUser",
                query = "SELECT u FROM User u " +
                        "WHERE u.id = :id"),
    @NamedQuery(name = "getUsers",
                query = "SELECT u FROM User u " +
                        "ORDER BY u.username"),
    @NamedQuery(name = "findUsers",
                query = "SELECT u FROM User u " +
                        "WHERE u.username LIKE :key " +
                        "ORDER BY u.username"),
    @NamedQuery(name = "countUsers",
                query = "SELECT COUNT(u.id) FROM User u"),

    /* user by ID */
    @NamedQuery(name = "getUserById",
                query = "SELECT u FROM User u " +
                        "WHERE u.id = :userId"),

    /* user by Name */
    @NamedQuery(name = "getUserByName",
                query = "SELECT u FROM User u " +
                        "WHERE u.username = :userName")
})
@XmlRootElement(name = "User")
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "USERNAME")
    @Size(min = 1, max = 50, message = "{user.username}")
    private String username;
    @Basic(optional = false)
    @Column(name = "GROUPNAME")
    @Size(min = 1, max = 50, message = "{user.groupname}")
    private String groupname;
    @Basic(optional = false)
    @Column(name = "PASSWORD")
    @Size(min = 1, max = 50, message = "{user.password}")
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @XmlTransient
    @JsonIgnore
    private List<Contact> contacts;

    public User() {
        this.contacts = new ArrayList<Contact>();
    }

    public User(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.contacts = new ArrayList<Contact>();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupname() {
        return this.groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) 
            return false;

        User other = (User)o;
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
        return "org.epalrov.addressbook.User[ id=" + this.id + " ]";
    }

}

