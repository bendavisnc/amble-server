# amble-server [![Build Status](https://github.com/bendavisnc/amble-server/actions/workflows/test.yml/badge.svg)](https://github.com/bendavisnc/amble-server/actions/workflows/test.yml)

This is a poc project that's a modest aim to give friends a way to play chinese checkers in almost real time with persistent state based on a relational database.

This is the backend part of the project, which is based on ring run directly on a jetty server with a sqlite database.

Feel free to reach out with any questions or ideas.

## Development

### to run the server locally with hotreloading enabled
```
clj -M:dev:run
```

There's also a Dockerfile available and a docker build and run script, `dockerrunlocal.sh` for docker-based local development.
