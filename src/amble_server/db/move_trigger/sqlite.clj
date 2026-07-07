(ns amble-server.db.move-trigger.sqlite
  "Provides callback for move table updates based on SQLite update listener.
   This is used to notify subscribers of move updates."
  (:require
    [amble-server.db.db :as db]
    [clojure.java.jdbc :as jdbc]
    [taoensso.timbre :as log])
  (:import
    (org.sqlite SQLiteUpdateListener)))

;; based on:
;;   https://github.com/xerial/sqlite-jdbc/blob/3d04d7df0c89240add2c92189adb30b6cb7e6ae0/src/test/java/org/sqlite/ListenerTest.java

(def subscribers (atom []))

(defn on-update
  [& args]
  (if (empty? @subscribers)
    (log/info ::on-update "No subscribers to update.")
    (do (log/info
         ::on-update
         (str "Updating " (count @subscribers) " move trigger subscriber\\s."))
        (let [rowid (last args)]
          (doseq [subscriber @subscribers]
            (subscriber rowid))))))

(def listener
  (reify
   SQLiteUpdateListener
     (onUpdate [_ t database table rowId]
       (on-update t database table rowId))))

(defn init!
  "Adds callback to subscribers state and registers the SQLite update listener."
  [callback]
  (jdbc/with-db-transaction [tx db/db]
                            (.addUpdateListener (:connection tx) listener)
                            (swap! subscribers conj callback)
                            nil))
