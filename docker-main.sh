#!/bin/bash
set -e

echo "Testing DB connection..."
PGPASSWORD="$POSTGRES_PASSWORD" psql -h "$POSTGRES_HOST" -U "$POSTGRES_USERNAME" -d "amble" -c '\l'

echo "Running migration..."
PGPASSWORD="$POSTGRES_PASSWORD" psql -h "$POSTGRES_HOST" -U "$POSTGRES_USERNAME" -d "amble" -f ./migrations/initfreshdb.pg.sql

echo "Starting app..."
exec java -jar app.jar
