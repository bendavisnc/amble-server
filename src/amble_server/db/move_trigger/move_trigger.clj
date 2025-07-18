(ns amble-server.db.move-trigger.move-trigger
  (:require
   [amble-server.config :as amble-config]
   [amble-server.db.move-trigger.postgres :as move-trigger-postgres]
   [amble-server.db.move-trigger.sqlite :as move-trigger-sqlite]))

(defn init![callback]
  (if amble-config/postgres?
    (move-trigger-postgres/init! callback)
    (move-trigger-sqlite/init! callback)))
  