rundevserver: clean initdb
	lein run

initdb:
	echo 'Initializing amble sql database'; \
	mkdir ../.amble-db
	cd ../.amble-db; \
	sqlite3 amble.db < ../amble-server/migrations/initfreshdb.sql

clean:
	rm -rf target
	rm -R -d -f ../.amble-db

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