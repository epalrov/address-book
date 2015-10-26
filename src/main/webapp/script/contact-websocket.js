/*
 * contact-websocket.js - address book front-end for WebSocket service
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

var app = angular.module('contactApp', ['ui.bootstrap']);

app.factory('$ws', ['$q', function($q) {
    return {
        post: function(url, obj) {
            var deferred = $q.defer();

            var ws = new WebSocket(url);
            ws.onopen = function(evt) {
                ws.send(JSON.stringify(obj));
            }
            ws.onmessage = function(evt) {
                deferred.resolve(JSON.parse(evt.data));
                ws.close();
            }
            ws.onerror = function(evt) {
                deferred.reject();
                ws.close();
            }
            return deferred.promise;
        }
    }
}]);

app.factory('contactService', ['$ws', function($ws) {
    var url = 'ws://' + location.host + '/address-book/ws-api/contacts/';
    return {
        query: function() {
            return $ws.post(url + 'read', { });
        },
        get: function(id) {
            return $ws.post(url + 'read', { 'id' : id });
        },
        save: function(contact) {
            return $ws.post(url + 'create', contact);
        },
        update: function(id, contact) {
            return $ws.post(url + 'update', contact);
        },
        delete: function(id) {
            return $ws.post(url + 'delete', { 'id': id });
        }
    }
}]);

app.controller('contactTableController', function($scope, $rootScope, $modal, contactService) {
    // table model
    $scope.contacts = [];
    // form model
    $scope.contact = {
        id : null,
        firstName : null,
        lastName : null,
        email : null
    };

    // CRUD operations (low level)
    // - creates a new contact entry
    $scope.contactCreate = function() {
        contactService.save($scope.contact).then(
            function(response) {
                $rootScope.$broadcast('success');
                $rootScope.$broadcast('refresh');
            },
            function() {
                $rootScope.$broadcast('error');
            }
        );
    };
    // - reads all the contact entries
    $scope.contactRead = function() {
        contactService.query().then(
            function(response) {
                $scope.contacts = angular.copy(response);
                $rootScope.$broadcast('success');
            }, 
            function(error) {
                $rootScope.$broadcast('error');
            }
	);
    };
    // - updates a contact entry
    $scope.contactUpdate = function() {
        contactService.get($scope.contact.id).then(
            function(response) {
                var contact = angular.copy(response);
                if ($scope.contact.firstName != null)
                    contact.firstName = $scope.contact.firstName;
                if ($scope.contact.lastName != null)
                    contact.lastName = $scope.contact.lastName;
                if ($scope.contact.email != null)
                    contact.email = $scope.contact.email;
                contactService.update($scope.contact.id, contact).then(
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
        contactService.delete($scope.contact.id).then(
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
            templateUrl: 'form.html',
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
    // - reads all the contact entries
    $scope.read = function() {
        $scope.contactRead();
    };
    // - updates a contact entry
    $scope.update = function(contact) {
        $modal.open({
            animation: true,
            templateUrl: 'form.html',
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
});

app.controller('contactFormController', function($scope, $modalInstance, contact) {
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
});

app.controller('contactAlertController', function($scope) {
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
});

