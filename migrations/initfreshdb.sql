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
	"rowid"	INTEGER PRIMARY KEY,
	"id"	TEXT,
	"game_id"	TEXT,
	"player_id"	TEXT,
	"player_piece_index"	TEXT,
	"x"	TEXT,
	"y"	TEXT,
	"client_id"	TEXT,
	"move"	TEXT,
	"is_nullified"	BOOLEAN,
	FOREIGN KEY("game_id") REFERENCES "Game"("id") ON DELETE CASCADE,
	UNIQUE("id", "game_id"),
	UNIQUE("client_id", "game_id")
);
COMMIT;
