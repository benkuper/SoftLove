#!/bin/bash
mvn clean install package
mvn exec:java
