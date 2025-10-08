docker build \
  --build-arg PORT=80 \
  --build-arg CLIENT_URL=http://localhost:8080 \
  --build-arg SQLITE_DB=amble-db/amble.db \
  --build-arg DBBACKUP=amble-db \
  --build-arg DBBACKUP_REMOTE=/app/testremote/amble-db \
  -t ambleserverdocker .

docker run \
  -e DATABASE=amble \
  -v /home/ben/home/programming/amble/testremote/amble-db:/app/testremote/amble-db \
  -p 80:80 \
  --add-host=host.docker.internal:host-gateway \
  ambleserverdocker:latest