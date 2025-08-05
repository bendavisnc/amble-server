docker build \
  --build-arg PORT=80 \
  --build-arg CLIENT_URL=http://localhost:8080 \
  --build-arg POSTGRES_SUBNAME=//host.docker.internal:5432/amble \
  --build-arg POSTGRES_USERNAME=admin \
  --build-arg POSTGRES_PASSWORD=admin \
  -t ambleserverdocker .

docker run \
  -e POSTGRES_HOST=host.docker.internal \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_USERNAME=admin \
  -e DATABASE=amble \
  -p 80:80 \
  --add-host=host.docker.internal:host-gateway \
  ambleserverdocker:latest