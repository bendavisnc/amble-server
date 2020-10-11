BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "Game" (
	"id"	TEXT,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "Move" (
	"id"	TEXT,
	"playerId"	TEXT,
	"gameId"	TEXT,
	"move"	TEXT,
	FOREIGN KEY("gameId") REFERENCES "Game"("id"),
	FOREIGN KEY("playerId") REFERENCES "Player"("id"),
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "Player" (
	"id"	TEXT,
	"gameId"	TEXT,
	FOREIGN KEY("gameId") REFERENCES "Game"("id")
);
COMMIT;
