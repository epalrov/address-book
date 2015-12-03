/*
 * contact.js - address book front-end main
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

//function () {
    if (window.location.search.indexOf('rest') >= 0) {
        document.write('<script src="js/contact-rest.js"><\/script>');
    } else if (window.location.search.indexOf('soap') >= 0) {
        document.write('<script src="js/contact-soap.js"><\/script>');
    } else if (window.location.search.indexOf('websocket') >= 0) {
        document.write('<script src="js/contact-websocket.js"><\/script>');
    } else {
        document.write('<script src="js/contact-rest.js"><\/script>');
    } 
//})()

