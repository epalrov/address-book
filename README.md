Address Book
============

Implementation of a simple Address Book as a single page application (SPA) within a thin server architecture (TSA). Modern Web technologies are combined and widely expolred in a splitted front-end/back-end architecture:

  - multiple Web interfaces: REST, SOAP, WebSockets.
  - multiple back-end implementations: JEE, Spring and Hibernate frameworks.
  - modern JEE technologies: JAX-RS, JAX-WS, WebSockets, Servlets, EJB, JPA.
  - modern front-end technologies: HTML, CSS, JavaScript.
  - modern front-end frameworks: AngularJS, RequireJS, Bootstrap.
  - modern build systems combined together: Maven, Grunt, Bower.

To install all the required dependencies check first [INSTALL.md].

Start the Glassfish application server
--------------------------------------

To start the locally installed Glassfish server and database:

  ```
  ~/glassfish4/glassfish/bin/asadmin start-database && \
  ~/glassfish4/glassfish/bin/asadmin start-domain --verbose
  ```

By default Glassfish uses a Derby database which can be inspected using the `ij` interactive SQL scripting tool through the command `connect 'jdbc:derby://localhost:1527/sun-appserv-samples;create=false';`. No authentication is required to access the default `sun-appserv-samples` database instance.

Build and deploy the Address Book project
-----------------------------------------

To build and deploy the complete Address Book project:

  ```
  mvn clean install
  ```

The Address Book front-end will be available at http://localhost:8080/address-book-angular

Contact
-------

paolorovelli@yahoo.it

[INSTALL.md]:INSTALL.md

