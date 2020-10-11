rundevserver: initdb
	lein ring server-headless

initdb: clean
	mkdir ../.amble-db
	cd ../.amble-db; \
	sqlite3 amble.db < ../amble-server/migrations/initfreshdb.sql

clean:
	rm -R -d -f ../.amble-db

runtests:
	echo 'Running amble server tests'; \
	lein auto test;
