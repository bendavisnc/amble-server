BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "game" (
	"id"	TEXT,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "player" (
	"id"	TEXT,
	"game_id"	TEXT,
	FOREIGN KEY("game_id") REFERENCES "game"("id") ON DELETE CASCADE,
	UNIQUE("id", "game_id")
);
CREATE TABLE IF NOT EXISTS "move" (
	"rowid" BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	"id"	TEXT,
	"game_id"	TEXT,
	"player_id"	TEXT,
	"player_piece_index"	TEXT,
	"x"	TEXT,
	"y"	TEXT,
	"client_id"	TEXT,
	"move"	TEXT,
	"is_nullified"	BOOLEAN,
	FOREIGN KEY("game_id") REFERENCES "game"("id") ON DELETE CASCADE,
	UNIQUE("id", "game_id"),
	UNIQUE("client_id", "game_id")
);
COMMIT;
