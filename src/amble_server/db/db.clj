(ns amble-server.db.db
  (:require
   [amble-server.config :as amble-config]))

(def sqlite-db-path "../.amble-db/amble.db")

(def sqlite-db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     sqlite-db-path
   :foreign_keys "on"})

(def postgres-db
  {:classname   "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname     (or amble-config/postgres-subname
                    (throw (new Exception "`postgres-subname` not set in environment variables.")))
   :user        amble-config/postgres-username
   :password    amble-config/postgres-password})

(def db
  (if amble-config/postgres?
    postgres-db
    sqlite-db))
