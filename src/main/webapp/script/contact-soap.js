/*
 * contact-soap.js - address book front-end for SOAP webservice
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

var app = angular.module('contactApp', ['ui.bootstrap']);

app.factory('$soap', ['$http', function($http) {
    return {
        post: function(url, ns, reqOp, reqObj, resOp, resObj) {
            return $http({
                method: 'POST',
                url: url,
                headers: { 'Content-Type': 'text/xml; charset=utf-8' },
                data: null, // filled by the interceptor
                // custom objects used by the interceptor
                soap: true,
                ns: ns,
                reqOp: reqOp,
                reqObj: reqObj,
                resOp: resOp,
                resObj: resObj
            });
        }
    }
}]);

app.factory('$soapInterceptor', ['$q', function($q) {
    return {
        'request': function(request) {
            // convert to XML and add SOAP envelope
            if (request.soap != undefined) {
                var start = '<?xml version="1.0" encoding="utf-8"?>'
                    + '<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">'
                    +    '<S:Body>';
                var stop = ''
                    +    '</S:Body>'
                    + '</S:Envelope>';
                var x2js = new X2JS();
                var reqXml = ''
                    + '<ns2:' + request.reqOp + ' xmlns:ns2="' + request.ns + '">'
                    + x2js.json2xml_str(request.reqObj)
                    + '</ns2:' + request.reqOp + '>'

                request.data = start + reqXml + stop;
            }
            return request;
        },
        'response': function(response) {
            // remove soap envelope and convert to JSON
            if (response.config.soap != undefined) {
                var start = response.data.search(/<S:Body>/g);
                var stop = response.data.search(/<\/S:Body>/g);
                // var substr = response.data.match(/(.*)<*:Body>(.*)<\/.*:Body>(.*)/g);
                if (start > 0 && stop > start) {
                    var x2js = new X2JS();
                    var resXml = response.data.slice(start + 8, stop);
                    var resJson = x2js.xml_str2json(resXml);
                    if (response.config.resOp)
                        resJson = resJson[response.config.resOp];
                    if (response.config.resObj)
                        resJson = resJson[response.config.resObj];
                    response = resJson;
                }
            }
            return response;
        }
    };
}]);

app.config(['$httpProvider', function($httpProvider) {  
    $httpProvider.interceptors.push('$soapInterceptor')
}]);

app.factory('contactService', ['$soap', function($soap) {
    var ns = 'http://addressbook.epalrov.org';
    var url = '/address-book/soap-api/ContactSoapService';
    return {
        query: function() {
            return $soap.post(url, ns,
                'GetContacts', { },
                'GetContactsResponse', 'contact');
        },
        get: function(id) {
            return $soap.post(url, ns,
                'GetContact', { 'id': id },
                'GetContactResponse', 'contact');
        },
        save: function(contact) {
            return $soap.post(url, ns,
                'CreateContact', { 'contact': contact },
                'CreateContactResponse', 'id');
        },
        update: function(id, contact) {
            return $soap.post(url, ns,
                'UpdateContact', { 'id': id, 'contact': contact },
                'UpdateContactResponse', 'contact');
        },
        delete: function(id) {
            return $soap.post(url, ns,
                'DeleteContact', { 'id': id },
                'DeleteContactResponse', '');
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

    // CRUD operations
    // - creates a new contact entry (low lewel)
    $scope.contactCreate = function() {
        contactService.save($scope.contact).then(
            function(response) {
                $rootScope.$broadcast('success');
                $rootScope.$broadcast('refresh');
            },
            function(error) {
                $rootScope.$broadcast('error');
            }
        );
    };
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
                    function(error) {
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
            function(error) {
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

