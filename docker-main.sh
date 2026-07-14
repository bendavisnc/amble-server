#!/bin/bash
set -e

echo "Cloning db backup..."

java -jar /app/dbbackupread.jar

if [ -z "$SQLITE_DB" ]; then
  echo "Error: SQLITE_DB environment variable is not set."
  exit 1
fi

DB_DIR=$(dirname "$SQLITE_DB")

if [ ! -f "$SQLITE_DB" ]; then
  echo "File $SQLITE_DB not found. Creating directory and empty file..."
  mkdir -p "$DB_DIR"
  touch "$SQLITE_DB"
  echo "Running migration..."
  sqlite3 "$SQLITE_DB" < migrations/initfreshdb.sql
else
  echo "Using db from clone backup. No migration run needed."
fi

echo "Starting server app..."
java -jar /app/server.jar &
SERVER_PID=$!

echo "Starting background db backup write process..."
java -jar /app/dbbackupwrite.jar &
DBBACKUPWRITE_PID=$!

# Trap Docker stop
trap 'echo "Container stopping…"; kill $SERVER_PID $DBBACKUPWRITE_PID; wait' TERM INT

# Wait for the server to exit
wait "$SERVER_PID"
