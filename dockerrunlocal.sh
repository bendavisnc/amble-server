docker build \
  --no-cache \
  --build-arg PORT=80 \
  --build-arg CLIENT_URL=http://localhost:8080 \
  --build-arg SQLITE_DB=amble-db/amble.db \
  --build-arg DBBACKUP=amble-db \
  -t ambleserverdocker .

docker run \
  -e DATABASE=amble \
  -p 3000:80 \
  --add-host=host.docker.internal:host-gateway \
  ambleserverdocker:latest