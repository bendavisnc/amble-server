# Use `make POSTGRES=true rundevserver` to switch to Postgres mode

POSTGRES ?= false

rundevserver: clean initdb
	lein run

initdb:
ifeq ($(POSTGRES),true)
	@echo 'Initializing amble PostgreSQL database'; \
	PGPASSWORD=admin psql -U admin -d amble -f migrations/initfreshdb.pg.sql
else
	@echo 'Initializing amble SQLite database'; \
	mkdir -p ../.amble-db; \
	cd ../.amble-db && sqlite3 amble.db < ../amble-server/migrations/initfreshdb.sql
endif

clean:
	rm -rf target
	rm -rf ../.amble-db

test--once:
	echo 'Running amble server tests'; \
	lein test;

test: test--once

test--watch:
	echo 'Running amble server tests, on every file change.'; \
	lein auto test;

rerundevserver:
	lein run

format:
	@echo "Formatting clj..."
	standard-clj fix src
