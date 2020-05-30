rundevserver: creategamedepotdir
	lein ring server-headless

creategamedepotdir: clean
	mkdir ../.amble-gamedepot
	cd ../.amble-gamedepot; \
	git init; \
	git checkout -b "amble-game-base"; \
	cp ../amble-server/resources/amble-gamedepot/game-base.json . ;\
	git add .; \
	git commit -m "Adds starting game."

clean:
	rm -R -d -f ../.amble-gamedepot