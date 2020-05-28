rundevserver: creategamedepotdir
	lein ring server-headless

creategamedepotdir: clean
	mkdir .amble-gamedepot
	cd .amble-gamedepot
	git init
	git checkout -b "amble-game-base"

clean:
	rm -r .amble-gamedepot