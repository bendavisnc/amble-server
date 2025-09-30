#!/bin/bash
set -e

echo "Running migration..."
echo 'Initializing amble SQLite database'

mkdir -p amble-db
sqlite3 amble-db/amble.db < migrations/initfreshdb.sql

echo "Starting app..."
exec java -jar /app/app.jar
