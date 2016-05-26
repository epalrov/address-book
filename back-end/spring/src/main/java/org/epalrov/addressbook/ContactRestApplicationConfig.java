/*
 * ContactRestApplicationConfig.java - address book RESTful webservice
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import org.epalrov.addressbook.Contact;
import org.epalrov.addressbook.ContactDataAccessObject;
import org.epalrov.addressbook.ContactDataAccessObjectInterface;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
 
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.SessionFactory;

import java.util.Properties;

/**
 * ContactRestApplicationConfig
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("org.epalrov.addressbook")
public class ContactRestApplicationConfig {

    @Bean(name = "dataSource")
    public DataSource getDataSource() throws NamingException {
        InitialContext ctx = new InitialContext();
        DataSource dataSource = (DataSource)ctx.lookup("jdbc/__default");
        return dataSource;
    }

    @Autowired
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory(DataSource dataSource) {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");

        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addProperties(properties);
        sessionBuilder.addAnnotatedClasses(Contact.class);
        return sessionBuilder.buildSessionFactory();
    }

    @Autowired
    @Bean(name = "transactionManager")
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Autowired
    @Bean(name = "contactDataAccessObject")
    public ContactDataAccessObject getContactDataAccessObject(SessionFactory sessionFactory) {
        return new ContactDataAccessObject(sessionFactory);
    }

}

