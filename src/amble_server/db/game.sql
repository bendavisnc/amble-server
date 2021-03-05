
-- name: game-add!
INSERT INTO game (id) VALUES (:id)

-- name: game-find-by-id
select * from game where id = :id
