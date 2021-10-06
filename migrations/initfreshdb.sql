BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "Game" (
	"id"	TEXT,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "Player" (
	"id"	TEXT,
	"game_id"	TEXT,
	FOREIGN KEY("game_id") REFERENCES "Game"("id") ON DELETE CASCADE,
	UNIQUE("id", "game_id")
);
CREATE TABLE IF NOT EXISTS "Move" (
	"id"	TEXT,
	"player_id"	TEXT,
	"game_id"	TEXT,
	"move"	TEXT,
--	FOREIGN KEY("game_id") REFERENCES "Game"("id") ON DELETE CASCADE,
	PRIMARY KEY("id"),
	FOREIGN KEY("game_id", "player_id") REFERENCES "Player"("game_id", "id") ON DELETE CASCADE
);
COMMIT;
