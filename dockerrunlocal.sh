docker build \
  --build-arg PORT=80 \
  --build-arg CLIENT_URL=http://localhost:8080 \
  --build-arg SQLITE_DB=amble-db/amble.db \
  --build-arg DBBACKUP=amble-db \
  --build-arg DBBACKUP_REMOTE=https://github.com/bendavisnc/amble-db.git \
  --build-arg DBBACKUP_PRIVATE_KEY=github_pat_000imnotreal \
  --build-arg DBBACKUP_USERNAME=bendavisnc \
  -t ambleserverdocker .

docker run \
  -e DATABASE=amble \
  -p 80:80 \
  --add-host=host.docker.internal:host-gateway \
  ambleserverdocker:latest