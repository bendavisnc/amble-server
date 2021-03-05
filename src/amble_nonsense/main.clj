(ns amble-nonsense.main
  (:require [amble-server.db.game-sql :as game-sql]))

(defn -main [& args]
  (println "hi")
  (println (game-sql/game-find-by-id)))
