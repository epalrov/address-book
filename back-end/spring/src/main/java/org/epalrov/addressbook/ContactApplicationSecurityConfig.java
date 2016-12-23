/*
 * ContactApplicationSecurityConfig.java - address book security configuration
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

package org.epalrov.addressbook;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class ContactApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("paolo")
                    .password("valeria")
                    .roles("USER", "ADMIN")
                    .and()
                .withUser("valeria")
                   .password("paolo")
                   .roles("USER");
    }

    @Override /* Overrides default HttpSecurity config. */
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
//                .antMatchers("/signup","/about").permitAll()
//                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
//                .and().formLogin()
                .and().httpBasic();

        http.csrf().disable(); // DISABLED CSRF protection to make it easier!
    }

    @Override /* Overrides default WebSecurity config. */
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers("/rest-api/users/**");
    }

}

//        http
//             .csrf().disable() // DISABLED CSRF protection to make it easier !
//             .authorizeRequests()
//             .antMatchers("/", "/login.html").permit
//             .anyRequest().authenticated()
//             .and()
//             .formLogin()
//             .loginPage("/login.html")
//             .permitAll()
//             .and()
//             .logout()
//             .permitAll()
//             .logoutUrl("/logout")
//             .logoutSuccessUrl("/");
//
//        auth
//            .jdbcAuthentication()
//            .dataSource(dataSource)
//            .usersByUsernameQuery(
//                "select username,password, enabled from users where username=?")
//            .authoritiesByUsernameQuery(
//                "select username, role from user_roles where username=?");
//
//        http
//            .authorizeRequests()
//            .antMatchers("/**").access("hasRole('ROLE_ADMIN')")
//            .and()
//                .formLogin().loginPage("/login").failureUrl("/login?error")
//                .usernameParameter("username").passwordParameter("password")
//            .and()
//                .logout().logoutSuccessUrl("/login?logout")
//            .and()
//                .exceptionHandling().accessDeniedPage("/403")
//            .and()
//                .csrf();

