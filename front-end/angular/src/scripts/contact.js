/*
 * contact.js - address book front-end main
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

require.config({
    baseUrl: '.',
    paths: {
        'angular' : [
            'https://ajax.googleapis.com/ajax/libs/angularjs/1.4.6/angular.min',
            'bower_components/angular/angular.min'],
        'ng-resource' : [
            'https://ajax.googleapis.com/ajax/libs/angularjs/1.4.6/angular-resource.min',
            'bower_components/angular-resource/angular-resource.min'],
        'ng-bootstrap' : [
            'https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.13.4/ui-bootstrap-tpls.min',
            'bower_components/angular-bootstrap/ui-bootstrap-tpls.min'],
        'xml2json' : [
            'https://x2js.googlecode.com/hg/xml2json.min',
            'bower_components/x2js/xml2json.min']
    },
    shim: {
        'angular' : {
            exports: 'angular'
        },
        'ng-resource' : ['angular'],
        'ng-bootstrap' : ['angular'],
        'xml2json' : {
            exports: 'x2js'
        }
    }
});

require([
    // Load the 'contact-rest' module
    'scripts/contact-rest'
], function(app){
    // this function is not fired until 'contact' dependencies have loaded
    app.init();
});

