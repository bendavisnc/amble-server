rundevserver: clean initdb
	lein run

initdb:
	mkdir ../.amble-db
	cd ../.amble-db; \
	sqlite3 amble.db < ../amble-server/migrations/initfreshdb.sql

clean:
	rm -rf target
	rm -R -d -f ../.amble-db

runtests:
	echo 'Running amble server tests'; \
	lein auto test;
