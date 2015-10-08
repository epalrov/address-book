/*
 * contact-soap.js - address book front-end for SOAP webservice
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

var app = angular.module('contactApp', ['ngResource', 'ui.bootstrap']);

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
                'GetContacts', '',
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

app.controller('contactTableController', function($scope, $rootScope, contactService) {
    // table model
    $scope.contacts = [];

    // returns all the contact entries
    $scope.contactReadAll = function() {
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
        $scope.contacts = $scope.contactReadAll();
    });

    $scope.contacts = $scope.contactReadAll();
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
        var contact = {
            id: 0,
            firstName: $scope.contact.firstName,
            lastName: $scope.contact.lastName,
            email: $scope.contact.email
        };
        contactService.save(contact).then(
            function(response) {
                $scope.formClear();
                $rootScope.$broadcast('success');
                $rootScope.$broadcast('refresh');
            },
            function(error) {
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
                $scope.formClear();
                $rootScope.$broadcast('success');
                $rootScope.$broadcast('refresh');
            },
            function(error) {
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

