/*
 * contact-rest.js - address book front-end for RESTful webservice
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

var app = angular.module('contactApp', ['ngResource', 'ui.bootstrap']);

app.factory('contactService', function($resource) {
    return $resource('rest-api/contacts/:id', { id: '@id' }, {
        update: {
            method: 'PUT'
        }
    });
});

app.controller('contactTableController', function($scope, $rootScope, contactService) {
    // table model
    $scope.contacts = [];

    // returns all the contact entries
    $scope.contactRead = function() {
        return contactService.query(
            function(response) {
                $scope.contacts = angular.copy(response);
                $rootScope.$broadcast('success');
            },
            function() {
                $rootScope.$broadcast('error');
            }
        );
    };

    // handle the refresh message
    $scope.$on('refresh', function () {
        $scope.contactRead();
    });

    $rootScope.$broadcast('refresh');
});

app.controller('contactFormController', function($scope, $rootScope, contactService) {
    // form model
    $scope.contact = {
        id : null, 
        firstName : null,
        lastName : null,
        email : null
    };

    // CRUD operations
    // - creates a new contact entry
    $scope.contactCreate = function() {
        $scope.entry = { };
        $scope.entry.firstName = $scope.contact.firstName;
        $scope.entry.lastName = $scope.contact.lastName;
        $scope.entry.email = $scope.contact.email;
        contactService.save($scope.entry,
            function(response) {
                $scope.formClear();
                $rootScope.$broadcast('success');
                $rootScope.$broadcast('refresh');
            },
            function() {
                $rootScope.$broadcast('error');
            }
        );
    };
    // - reads a single contact entry
    $scope.contactRead = function() {
        contactService.get({ id: $scope.contact.id });
    };
    // - updates a contact entry
    $scope.contactUpdate = function() {
        $scope.entry = contactService.get({ id: $scope.contact.id },
            function(response) {
                if ($scope.contact.firstName != null)
                    $scope.entry.firstName = $scope.contact.firstName;
                if ($scope.contact.lastName != null)
                    $scope.entry.lastName = $scope.contact.lastName;
                if ($scope.contact.email != null)
                    $scope.entry.email = $scope.contact.email;
                // note the instance method for the update operation,
                // in contrast to "static" class methods (see
                // https://docs.angularjs.org/api/ngResource/service/$resource)
                // used for the provided operations (query, get, save, delete)
                $scope.entry.$update({ id: $scope.contact.id }, $scope.contact,
                    function(response) {
                        $scope.formClear();
                        $rootScope.$broadcast('success');
                        $rootScope.$broadcast('refresh');
                    },
                    function() {
                        $rootScope.$broadcast('error');
                    }
                );
            },
            function() {
                $rootScope.$broadcast('error');
            }
        );
    };
    // - deletes a contact entry
    $scope.contactDelete = function() {
        contactService.delete({ id: $scope.contact.id },
            function(response) {
                $scope.formClear();
                $rootScope.$broadcast('success');
                $rootScope.$broadcast('refresh');
            },
            function() {
                $rootScope.$broadcast('error');
            }
        );
    };

    $scope.formClear = function() {
       $scope.contact.id = null;
       $scope.contact.firstName = null;
       $scope.contact.lastName = null;
       $scope.contact.email = null;
    };

});

app.controller('contactAlertController', function ($scope) {
    // message handler
    // - operation success
    $scope.$on('success', function () {
        $scope.alerts = [
            { type: 'success', msg: 'Server ok!' }
        ];
    });
    // - operation failure
    $scope.$on('error', function () {
        $scope.alerts = [
            { type: 'danger', msg: 'Server failure!' }
        ];
    });

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };
});

