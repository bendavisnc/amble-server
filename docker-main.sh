#!/bin/bash
set -e

echo "Cloning db backup..."
git config --global --add safe.directory /app/testremote/amble-db

java -jar /app/dbbackupread.jar

echo "Running migration..."
sqlite3 amble-db/amble.db < migrations/initfreshdb.sql

echo "Starting server app..."
java -jar /app/server.jar &
SERVER_PID=$!

echo "Starting background db backup write process..."
java -jar /app/dbbackupwrite.jar &
DBBACKUPWRITE_PID=$!

# Trap Docker stop
trap 'echo "Container stopping…"; kill $SERVER_PID; kill $DBBACKUPWRITE_PID; wait' TERM INT

# Wait for both processes
wait -n
