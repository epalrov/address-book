/*
 * contact-rest.js - address book front-end for RESTful webservice
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

var app = angular.module('contactApp', ['ngResource', 'ui.bootstrap']);

app.factory('contactService', ['$resource', function($resource) {
    return $resource('rest-api/contacts/:id', { id: '@id' }, {
        update: {
            method: 'PUT'
        }
    });
}]);

app.controller('contactController', ['$scope', '$rootScope', '$modal',
        'contactService', function($scope, $rootScope, $modal, contactService) {
    // table model
    $scope.contacts = [];
    // search form model
    $scope.key = "";
    // create/update form model
    $scope.contact = {
        id : null, 
        firstName : null,
        lastName : null,
        email : null
    };

    // CRUD operations (low lewel)
    // - creates a new contact entry
    $scope.contactCreate = function() {
        $scope.entry = { };
        $scope.entry.firstName = $scope.contact.firstName;
        $scope.entry.lastName = $scope.contact.lastName;
        $scope.entry.email = $scope.contact.email;
        contactService.save($scope.entry,
            function(response) {
                $rootScope.$broadcast('success');
                $rootScope.$broadcast('refresh');
            },
            function() {
                $rootScope.$broadcast('error');
            }
        );
    };
    // - returns all the contact entries matching the key
    $scope.contactRead = function() {
        return contactService.query({ key : $scope.key },
            function(response) {
                $scope.contacts = angular.copy(response);
                $rootScope.$broadcast('success');
            },
            function() {
                $rootScope.$broadcast('error');
            }
        );
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
                $rootScope.$broadcast('success');
                $rootScope.$broadcast('refresh');
            },
            function() {
                $rootScope.$broadcast('error');
            }
        );
    };

    // CRUD operations (high lewel)
    // - creates a new contact entry
    $scope.create = function() {
        $modal.open({
            animation: true,
            templateUrl: 'tplt/contact-form.html',
            controller: 'contactFormController',
            resolve: {
                contact: function() {
                    // init an empty form
                    $scope.contact.id = 0;
                    $scope.contact.firstName = null;
                    $scope.contact.lastName = null;
                    $scope.contact.email = null;
                    return $scope.contact;
                }
            }
        }).result.then(
            function(contact) { // modal close (Ok): create the new contact
                $scope.contactCreate();
            },
            function() { // modal dismiss (Cancel): do nothing
            }
        );
    };
    // - retrieves all the contact entries matching the key
    $scope.read = function(key) {
        $scope.key = angular.copy(key);
        $scope.contactRead();
    };
    // - updates a contact entry
    $scope.update = function(contact) {
        $modal.open({
            animation: true,
            templateUrl: 'tplt/contact-form.html',
            controller: 'contactFormController',
            resolve: {
                contact: function() {
                    // init the form with the current values
                    $scope.contact = angular.copy(contact);
                    return $scope.contact;
                }
            }
        }).result.then(
            function(contact) { // modal close (Ok): update the contact
                $scope.contact = angular.copy(contact);
                $scope.contactUpdate();
            },
            function() { // modal dismiss (Cancel): do nothing
            }
        );
    };
    // - deletes a contact entry
    $scope.delete = function(contact) {
        $scope.contact = angular.copy(contact);
        $scope.contactDelete();
    };

    // handle the refresh message
    $scope.$on('refresh', function () {
        $scope.contactRead();
    });

    $rootScope.$broadcast('refresh');
}]);

app.controller('contactFormController', ['$scope', '$modalInstance', 'contact',
        function($scope, $modalInstance, contact) {
    // form model
    $scope.contact = contact;

    // - button Ok: close the form returning the new inputs
    $scope.ok = function() {
        $modalInstance.close($scope.contact);
    };
    // - button Cancel: simply dismiss the form
    $scope.cancel = function() {
        $modalInstance.dismiss();
    };
}]);

app.controller('contactAlertController', ['$scope', function($scope) {
    // message handler
    // - operation success
    $scope.$on('success', function() {
        $scope.alerts = [
            { type: 'success', msg: 'Server ok!' }
        ];
    });
    // - operation failure
    $scope.$on('error', function() {
        $scope.alerts = [
            { type: 'danger', msg: 'Server failure!' }
        ];
    });

    $scope.close = function(index) {
        $scope.alerts.splice(index, 1);
    };
}]);

