
-- name: player-add!
INSERT INTO player (id, "game_id") VALUES (:id, :game_id)

-- name: player-find-by-game-id
select * from player where "game_id" = :game_id

-- name: player-find-by-id
select * from player where  "game_id" = :game_id and id = :id
