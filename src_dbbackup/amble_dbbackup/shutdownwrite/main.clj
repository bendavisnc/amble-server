(ns amble-dbbackup.shutdownwrite.main
  (:gen-class) 
  (:require
   [amble-dbbackup.config :as config]
   [com.twinql.clojure.git :as git]))

(defn write []
  (git/with-repo config/dbbackup
    (println (git/push config/dbbackup-remote))))


(defn -main [& _]
  (.addShutdownHook (Runtime/getRuntime)
    (new Thread
      (fn []
        (write)
        (println "Database backup read successfully."))))
  (println "Shutdown handler started, waiting for SIGTERM...")
  (let [lock (new Object)]
    (locking lock
      (.wait lock))))


