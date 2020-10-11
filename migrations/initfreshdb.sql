BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "Game" (
	"id"	TEXT,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "Person" (
	"id"	TEXT,
	"gameId"	TEXT,
	FOREIGN KEY("gameId") REFERENCES "Game"("id")
);
CREATE TABLE IF NOT EXISTS "Move" (
	"id"	TEXT,
	"playerId"	TEXT,
	"gameId"	TEXT,
	"move"	TEXT,
	FOREIGN KEY("playerId") REFERENCES "Person"("id"),
	FOREIGN KEY("gameId") REFERENCES "Game"("id"),
	PRIMARY KEY("id")
);
COMMIT;
