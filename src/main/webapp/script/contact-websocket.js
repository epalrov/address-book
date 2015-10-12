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
    var url = 'ws://localhost:8080/address-book/ws-api/contacts/';
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

app.controller('contactTableController', function($scope, $rootScope, contactService) {
    // table model
    $scope.contacts = [];

    // returns all the contact entries
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
        contactService.save($scope.contact).then(
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
        contactService.get($scope.contact.id);
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
        contactService.delete($scope.contact.id).then(
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

