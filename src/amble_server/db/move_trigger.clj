(ns amble-server.db.move-trigger
  (:require [clojure.java.io :as io]
            [clojure.reflect :as reflect]
            [clojure.java.jdbc :as jdbc] 
            [amble-server.db.core :as db])
             
  (:import [java.sql DriverManager]
           [org.sqlite SQLiteUpdateListener]))

;; (go-loop [connection]
;;   (.addUpdateListener connection on-update))

;; based on:
;;   https://github.com/xerial/sqlite-jdbc/blob/3d04d7df0c89240add2c92189adb30b6cb7e6ae0/src/test/java/org/sqlite/ListenerTest.java

(def target-db-path "../.amble-db/amble.db")

(def subscribers (atom []))

(defn on-update [& args]
  (if (empty? @subscribers)
    (println "No subscribers to update.")
    (do (println (str "Updating " (count @subscribers) " subscriber\\s."))
      (doseq [subscriber @subscribers]
        (let [move-id (dec (last args))] 
          (subscriber move-id))))))
  
(def listener
              (reify SQLiteUpdateListener
                (onUpdate [this, t, database, table, rowId]
                  (amble-server.db.move-trigger/on-update t, database, table, rowId))))

(defn init! [subscriber-notify-fn]
  (jdbc/with-db-transaction [tx db/db]
    (.addUpdateListener (:connection tx) listener) 
    (swap! subscribers conj subscriber-notify-fn)
    nil))
