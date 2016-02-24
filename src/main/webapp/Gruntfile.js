/*
 * Gruntfile.js - address book front-end build system
 * 
 * Copyright (C) 2015 Paolo Rovelli 
 * 
 * Author: Paolo Rovelli <paolorovelli@yahoo.it> 
 */

'use strict';

module.exports = function (grunt) {

  require('time-grunt')(grunt);
  require('jit-grunt')(grunt);

  grunt.initConfig({

    /* Watches files for changes and runs tasks based on the changed files */
    watch: {
      js: {
        files: ['./scripts/{,*/}*.js'],
        tasks: ['newer:jshint:all', 'newer:jscs:all'],
        options: {
          livereload: '<%= connect.options.livereload %>'
        }
      },
      gruntfile: {
        files: ['Gruntfile.js']
      },
      livereload: {
        options: {
          livereload: '<%= connect.options.livereload %>'
        },
        files: [
          './{,*/}*.html',
          './styles/{,*/}*.css',
          './images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
        ]
      }
    },

    /* The grunt server settings */
    connect: {
      options: {
        port: 9000,
        hostname: 'localhost',
        livereload: 35729
      },
      livereload: {
        options: {
          open: true
        }
      }
    },

    /* Make sure there are no obvious mistakes */
    jshint: {
      options: {
        jshintrc: '.jshintrc',
        reporter: require('jshint-stylish')
      },
      all: {
        src: [
          'Gruntfile.js',
          './scripts/{,*/}*.js'
        ]
      }
    },

    /* Make sure code styles are up to par */
    jscs: {
      options: {
        config: '.jscsrc',
        verbose: true
      },
      all: {
        src: [
          'Gruntfile.js',
          './scripts/{,*/}*.js'
        ]
      }
    }

  });

  grunt.registerTask('serve', 'start a connect web server', function() {
    grunt.task.run([
      'connect:livereload',
      'watch'
    ]);
  });

  grunt.registerTask('default', [
    'newer:jshint',
    'newer:jscs'
  ]);
};

