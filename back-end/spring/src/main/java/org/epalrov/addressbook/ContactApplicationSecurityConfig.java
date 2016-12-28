/*
 * ContactApplicationSecurityConfig.java - address book security configuration
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class ContactApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDataAccessObjectInterface userDao;

    @Override /* Overrides default AuthenticationManager config */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*
         * Registers our custom user details service as an anonymous class,
         * which overrides the methods used to retrieve user details from the
         * database according to the specified username
         */
        auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                try {
                    User user = userDao.getUserByName(username);
                    GrantedAuthority authority = new SimpleGrantedAuthority(user.getGroupname());
                    return new org.springframework.security.core.userdetails.User(
                        user.getUsername(), user.getPassword(), Arrays.asList(authority));
                } catch (Exception e) {
                    throw new UsernameNotFoundException("User " + username + " not found.");
                } 
            }
        });
    }

    @Override /* Overrides default HttpSecurity config. */
    protected void configure(HttpSecurity http) throws Exception {
        /*
         * Configure Spring Security in order to:
         *  - ensure that any request requires the user to be authenticated
         *  - allow users to authenticate with HTTP Basic authentication
         *  - disable Cross Site Request Forgery (CSRF) protection to make things easier
         */
        http
            .authorizeRequests().anyRequest().authenticated()
                .and()
            .httpBasic()
                .and()
            .csrf().disable();
    }

    @Override /* Overrides default WebSecurity config. */
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring().antMatchers("/rest-api/users/**");
    }

}

