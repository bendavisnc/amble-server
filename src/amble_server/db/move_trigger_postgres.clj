(ns amble-server.db.move-trigger-postgres
  "Provides callback for move table updates based on PostgreSQL LISTEN/NOTIFY.
   This is used to notify subscribers of move updates."
  (:require
   [amble-server.db.db :as db]
   [clojure.java.jdbc :as jdbc]
   [clojure.core.async :as core-async])
  (:import
   (org.postgresql PGNotification PGConnection)
   (org.apache.logging.log4j LogManager)))

(def log (. LogManager getLogger "amble-server.move-trigger-postgres"))

(def subscribers (atom []))

(defn start-listener-loop! []
  (core-async/go-loop []
    (try
      (jdbc/with-db-connection [con-db db/db]
        (let [conn (-> con-db :connection)]
          (.createStatement conn)
          (.execute (.createStatement conn) "LISTEN move_update")
          (loop []
            (when-let [notifications (.getNotifications conn)]
              (doseq [^PGNotification notif notifications]
                (let [rowid (Long/parseLong (.getParameter notif))]
                  (.info log (str "Received notification: " rowid))
                  (doseq [callback @subscribers]
                    (try
                      (callback rowid)
                      (catch Exception e
                        (.warn log "Callback failed" e)))))))
            (core-async/<! (core-async/timeout 500)) ; Wait before checking again
            (recur))))
      (catch Exception e
        (.error log "Error in notification loop" e)))
    (core-async/<! (core-async/timeout 10000)) ; Wait before retrying on failure
    (.info log "Restarting notification listener...")
    (recur)))

(defn init!
  "Adds a callback to the notification subscribers list."
  [callback]
  (swap! subscribers conj callback)
  (start-listener-loop!)
  nil)
