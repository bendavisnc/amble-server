
-- name: move-add!
INSERT INTO move ("game_id", "player_id", "player_piece_index", "id", "move", "x", "y", "client_id") VALUES (:game_id, :player_id, :player_piece_index, :id, :move, :x, :y, :client_id)

-- name: move-find-by-id
select * from move where "game_id" = :game_id and id = :id

-- name: move-count
select count(*) as count from move where "game_id" = :game_id

