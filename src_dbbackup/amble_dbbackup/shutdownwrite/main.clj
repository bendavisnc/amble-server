(ns amble-dbbackup.shutdownwrite.main
  (:gen-class) 
  (:require
   [amble-dbbackup.config :as config]
   [clj-jgit.porcelain :as git]))

(defn write []
  (let [repo (git/load-repo config/dbbackup)]
    (git/git-add repo ".")
    (git/git-commit repo "Backup update")
    (git/git-push repo "origin" "main")
    (println "Backup pushed successfully.")))

(defn -main [& _]
  (.addShutdownHook (Runtime/getRuntime)
    (new Thread
      (fn []
        (write)
        (println "Successfully handled shutdown."))))
  (println "Shutdown handler started, waiting for SIGTERM...")
  (let [lock (new Object)]
    (locking lock
      (.wait lock))))


