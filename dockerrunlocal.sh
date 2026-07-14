docker build \
  --no-cache \
  --progress=plain \
  --build-arg PORT=80 \
  --build-arg CLIENT_URL=http://localhost:8080 \
  --build-arg SQLITE_DB=amble-db/amble.db \
  --build-arg DBBACKUP=amble-db \
  --build-arg DBBACKUP_REMOTE=/app/testremote/amble-db \
  --build-arg DBBACKUP_USERNAME=dummydbusername \
  --build-arg DBBACKUP_PRIVATE_KEY=dummydbprivatekey \
  -t ambleserverdocker .

docker run \
  -e DATABASE=amble \
  -p 3000:80 \
  -v "$(pwd)/../../amble-db:/app/testremote/amble-db" \
  --add-host=host.docker.internal:host-gateway \
  ambleserverdocker:latest