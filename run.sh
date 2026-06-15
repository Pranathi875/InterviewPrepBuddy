#!/bin/bash

# Interview Prep Buddy - Build and Run Script
# Prerequisites:
#   - Java 17+
#   - MySQL running with the schema applied (see schema.sql)
#   - MySQL Connector/J JAR in the lib/ folder

# Load environment variables from .env file if it exists
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

# Download MySQL connector if not present
if [ ! -f lib/mysql-connector-j.jar ]; then
    echo "MySQL Connector JAR not found in lib/."
    echo "Download it from: https://dev.mysql.com/downloads/connector/j/"
    echo "Place it at: lib/mysql-connector-j.jar"
    exit 1
fi

# Compile
echo "Compiling..."
javac -cp "lib/*" -d out src/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# Run
echo "Starting Interview Prep Buddy..."
echo ""
java -cp "out:lib/*" Main
