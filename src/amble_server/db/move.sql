
-- name: move-add!
INSERT INTO move ("game_id", "player_id", id, move) VALUES (:game_id, :player_id, :id, :move)

-- name: move-find-by-id
select * from move where "game_id" = :game_id  and "player_id" = :player_id  and id = :id
