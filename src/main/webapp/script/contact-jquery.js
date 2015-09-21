/*
 * contact-jquery.js - address book front-end using jQuery
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

function contactInstance(id, firstName, lastName, email) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
}

function createContactsTable(header, body) {
    var table = $('<table/>').addClass('table');
  
    // add header to table
    var row = $('<tr/>');
    $.each(header, function(idx, h) { 
        row.append($('<th/>').text(h));
    });
    table.append(row);
  
    // add rows to table
    $.each(body, function(idx, r) {
        var row = $('<tr/>');
        // add colums to row
        $.each(r, function(idx, c) { 
            row.append($('<td/>').text(c));
        });
        table.append(row);
    });
  
    return table;
}

function showContactsTable(container) {
    // get contacts from the web service
    $.ajax({
        type:'GET',
        url:'webapi/contacts',
        contentType:'application/json',
        dataType:'json'
    }).done(function(data) {
        // contacts table header
        var header = ['Id', 'First Name', 'Last Name', 'Email Address'];
  
        // contacts table body
        var body = [];
        $.each(data, function (idx, contact) {
            body[idx] = new contactInstance(contact.id, contact.firstName,
                contact.lastName, contact.email);
        });
  
        // create contacts table
        var table = createContactsTable(header, body);
  
        // show contacts table
        container.html(table);
    });
}

function handleContactCreate(container) {
    var form = container.find('form');
    var button = form.find('button');

    button.click(function() {
        // create the new contact
        var contact = new contactInstance(
            form.find('#id').val(),
            form.find('#firstName').val(),
            form.find('#lastName').val(),
            form.find('#email').val()
        );
        // post it to the web service
        $.ajax({
            type:'POST',
            url:'webapi/contacts',
            data:JSON.stringify(contact),
            contentType:'application/json',
            dataType:'json',
            async: false
        });
    });
}

function handleContactUpdate(container) {
    var form = container.find('form');
    var button = form.find('button');
  
    button.click(function() {
        // get the contact to update
        var id = form.find('#id').val();
        // update the contact
        var contact = new contactInstance(
            form.find('#id').val(),
            form.find('#firstName').val(),
            form.find('#lastName').val(),
            form.find('#email').val()
        );
        // post it to the web service
        $.ajax({
              type:'PUT',
              url:'webapi/contacts/'+id,
              data:JSON.stringify(contact),
              contentType:'application/json',
              dataType:'json',
              async: false
          });
      });
}

function handleContactDelete(container) {
    var form = container.find('form');
    var button = form.find('button');
  
    button.click(function() {
        // get the contact to remove
        var id = form.find('#id').val();
        // delete it from the web service
        $.ajax({
            type:'DELETE',
            url:'webapi/contacts/'+id,
            contentType:'application/json',
            dataType:'json',
            async: false
        });
    });
}

$(document).ready(function() {
    showContactsTable($('#address-book-table'));
    handleContactCreate($('#address-book-create'));
    handleContactUpdate($('#address-book-update'));
    handleContactDelete($('#address-book-delete'));
});
