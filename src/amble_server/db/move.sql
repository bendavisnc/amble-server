
-- name: move-add!
INSERT INTO move ("game_id", "player_id", "player_piece_index", "id", "move", "is_nullified", "x", "y", "client_id") VALUES (:game_id, :player_id, :player_piece_index, :id, :move, :is_nullified, :x, :y, :client_id)

-- name: move-find-by-player-id
select * from move where "game_id" = :game_id and "player_id" = :player_id and "is_nullified" = :is_nullified

-- name: move-find-by-game-id
select * from move where "game_id" = :game_id and "is_nullified" = :is_nullified

-- name: move-delete-by-id!
update move set "is_nullified" = true where "game_id" = :game_id and "id" = :id and "is_nullified" = false

-- name: move-find-by-id
select * from move where "game_id" = :game_id and id = :id and "is_nullified" = :is_nullified

-- name: move-find-by-rowid
select * from move where "rowid" = :rowid

-- name: move-count
select count(*) as count from move where "game_id" = :game_id

