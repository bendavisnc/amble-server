(ns amble-server.db.move-trigger
  (:require [clojure.java.io :as io]
            [clojure.reflect :as reflect]
            [clojure.java.jdbc :as jdbc]
            [amble-server.db.core :as db])

  (:import [java.sql DriverManager]
           [org.sqlite SQLiteUpdateListener]
           [org.apache.logging.log4j LogManager]))

;; (go-loop [connection]
;;   (.addUpdateListener connection on-update))

;; based on:
;;   https://github.com/xerial/sqlite-jdbc/blob/3d04d7df0c89240add2c92189adb30b6cb7e6ae0/src/test/java/org/sqlite/ListenerTest.java

(def log (. LogManager getLogger "amble-server.move-trigger"))

(def subscribers (atom []))

(defn on-update [& args]
  (if (empty? @subscribers)
    (.info log "No subscribers to update.")
    (do (.info log (str "Updating " (count @subscribers) " move trigger subscriber\\s."))
        (doseq [subscriber @subscribers]
          (let [move-id (last args)]
            (subscriber move-id))))))

(def listener
  (reify SQLiteUpdateListener
    (onUpdate [this, t, database, table, rowId]
      (amble-server.db.move-trigger/on-update t, database, table, rowId))))

(defn init!
  "Initializes `on-update` to be called "
  [subscriber-notify-fn]
  (jdbc/with-db-transaction [tx db/db]
    (.addUpdateListener (:connection tx) listener)
    (swap! subscribers conj subscriber-notify-fn)
    nil))
