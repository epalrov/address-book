Address Book dependencies
=========================

This document describes how to install the Address Book project dependencies
on Ubuntu 14.04 LTS. These instructions should also work on other Debian
derivative distributions.

Requirements
------------

To run the Address Book project you will need the following software:

  - java-jdk: the Java development kit.

      ```
      sudo apt-get install openjdk-7-jdk
      ```

  - maven: the Apache build manager for Java projects.

      ```
      wget https://archive.apache.org/dist/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz
      sudo tar xzvf apache-maven-3.3.3-bin.tar.gz -C /usr/share/
      sudo update-alternatives --install /usr/bin/mvn mvn /usr/share/apache-maven-3.3.3/bin/mvn 150
      sudo update-alternatives --config mvn
      ```

  - glassfish: the Oracle open-source application server.

      ```
      wget http://download.java.net/glassfish/4.1/release/glassfish-4.1.zip
      unzip glassfish-4.1.zip
      ```

