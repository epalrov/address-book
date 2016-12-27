/*
 * ContactApplicationConfig.java - address book application configuration
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
 
import org.hibernate.SessionFactory;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Properties;

/**
 * ContactApplicationConfig
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("org.epalrov.addressbook")
public class ContactApplicationConfig {

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        /* 
         * Glassfish already provides the Derby jdbc driver and a default data
         * source object linked to the "sun-appserv-samples" database, which
         * requires no authenication. So, we just need to retrieve the data
         * source object from a JNDI naming service.
         */
        try { 
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/__default");
            return ds;
        } catch (NamingException e) {
            /* In case of failure try to build a new data source object */
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
            ds.setUrl("jdbc:derby://localhost:1527/sun-appserv-samples");
            ds.setUsername("");
            ds.setPassword("");
            return ds;
        }
    }

    @Autowired
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory(DataSource ds) {
        /*
         * Setup Hibernate as ORM over the data source object by using
         * sessions: a session opens a single database connection when it is
         * created, and holds onto it until the session is closed. Every
         * object that is loaded by Hibernate from the database is associated
         * with the session.
         */
        LocalSessionFactoryBuilder sb =
            new LocalSessionFactoryBuilder(ds);

        /*
         * Configure Hibernate using the following featuers:
         *  - log SQL statements.
         *  - use Derby SQL dialect.
         *  - generate database tables from JPA annotations.
         *  - populate database tables with initial values.
         */
        Properties p = new Properties();
        p.put("hibernate.show_sql", "true");
        p.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        p.put("hibernate.hbm2ddl.auto", "create");
        p.put("hibernate.hbm2ddl.import_files", "META-INF/sql/data.sql");
        sb.addProperties(p);

        /*
         * Manually add all the Entity classes to Hibernate: in addition to
         * its own "native" API, Hibernate is also an implementation of the
         * Java Persistence API (JPA) specification.
         */
        sb.addAnnotatedClasses(Contact.class);
        sb.addAnnotatedClasses(User.class);

        /* Return a session factory */
        return sb.buildSessionFactory();
    }

    @Autowired
    @Bean(name = "transactionManager")
    public HibernateTransactionManager getTransactionManager(SessionFactory sf) {
        return new HibernateTransactionManager(sf);
    }

    @Autowired
    @Bean(name = "userDataAccessObject")
    public UserDataAccessObject getUserDataAccessObject(SessionFactory sf) {
        return new UserDataAccessObject(sf);
    }

    @Autowired
    @Bean(name = "contactDataAccessObject")
    public ContactDataAccessObject getContactDataAccessObject(SessionFactory sf) {
        return new ContactDataAccessObject(sf);
    }

}

