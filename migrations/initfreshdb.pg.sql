BEGIN;

CREATE TABLE IF NOT EXISTS "game" (
    "id" TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS "player" (
    "id" TEXT,
    "game_id" TEXT,
    FOREIGN KEY("game_id") REFERENCES "game"("id") ON DELETE CASCADE,
    UNIQUE("id", "game_id")
);

CREATE TABLE IF NOT EXISTS "move" (
    "rowid" BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    "id" TEXT,
    "game_id" TEXT,
    "player_id" TEXT,
    "player_piece_index" TEXT,
    "x" TEXT,
    "y" TEXT,
    "client_id" TEXT,
    "move" TEXT,
    "is_nullified" BOOLEAN,
    FOREIGN KEY("game_id") REFERENCES "game"("id") ON DELETE CASCADE,
    UNIQUE("id", "game_id"),
    UNIQUE("client_id", "game_id")
);

COMMIT;

-- trigger function
CREATE OR REPLACE FUNCTION notify_move_update()
RETURNS trigger AS $$
BEGIN
  PERFORM pg_notify('move_update', NEW.rowid::text);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- trigger
CREATE TRIGGER move_update_trigger
AFTER INSERT OR UPDATE ON move
FOR EACH ROW
EXECUTE FUNCTION notify_move_update();
