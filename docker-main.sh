#!/bin/bash
set -e

echo "Running migration..."
mkdir -p amble-db
sqlite3 amble-db/amble.db < migrations/initfreshdb.sql

echo "Starting server app..."
java -jar /app/server.jar &
SERVER_PID=$!

echo "Starting shutdown handler app..."
java -jar /app/shutdown.jar &
SHUTDOWN_PID=$!

# Trap Docker stop
trap 'echo "Container stopping…"; kill $SERVER_PID; kill $SHUTDOWN_PID; wait' TERM INT

# Wait for both processes
wait -n
