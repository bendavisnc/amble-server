(ns amble-server.db.move-trigger
  "Provides callback for move table updates based on SQLite update listener.
   This is used to notify subscribers of move updates."
  (:require
   [amble-server.db.core :as db]
   [clojure.java.io :as io]
   [clojure.java.jdbc :as jdbc]
   [clojure.reflect :as reflect])
  (:import
   (java.sql DriverManager)
   (org.apache.logging.log4j LogManager)
   (org.sqlite SQLiteUpdateListener)))

;; based on:
;;   https://github.com/xerial/sqlite-jdbc/blob/3d04d7df0c89240add2c92189adb30b6cb7e6ae0/src/test/java/org/sqlite/ListenerTest.java

(def log (. LogManager getLogger "amble-server.move-trigger"))

(def subscribers (atom []))

(defn on-update [& args]
  (if (empty? @subscribers)
    (.info log "No subscribers to update.")
    (do (.info log (str "Updating " (count @subscribers) " move trigger subscriber\\s."))
        (let [rowid (last args)]
          (doseq [subscriber @subscribers]
            (subscriber rowid))))))

(def listener
  (reify SQLiteUpdateListener
    (onUpdate [this, t, database, table, rowId]
      (amble-server.db.move-trigger/on-update t, database, table, rowId))))

(defn init!
  "Adds callback to subscribers state and registers the SQLite update listener."
  [callback]
  (jdbc/with-db-transaction [tx db/db]
    (.addUpdateListener (:connection tx) listener)
    (swap! subscribers conj callback)
    nil))
