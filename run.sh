#!/usr/bin/env bash
# ./mvnw clean install -U
./mvnw clean compile test exec:java
