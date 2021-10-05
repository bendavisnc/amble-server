(ns amble-server.db.core)

(def target-db-path "../.amble-db/amble.db")

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     target-db-path
   :foreign_keys "on"})
