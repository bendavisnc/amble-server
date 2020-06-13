rundevserver: creategamedepot
	lein ring server-headless

creategamedepot: clean
	mkdir ../.amble-gamedepot
	cd ../.amble-gamedepot; \
	git init; \
	git checkout -b "amble-game-base"; \
	cp ../amble-server/resources/amble-gamedepot/game-base.json . ;\
	git add .; \
	git commit -m "Adds starting game."

clean:
	rm -R -d -f ../.amble-gamedepot

runtests:
	echo 'Running amble server tests'; \
	lein auto test;
