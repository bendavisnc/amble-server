(ns amble-dbbackup.shutdownwrite.main
  (:gen-class) 
  (:require
   [amble-dbbackup.config :as config]
   [clj-jgit.porcelain :as git]))

(defn write []
  (let [repo (git/load-repo config/dbbackup)]
    (git/git-add repo ".")
    (git/git-commit repo "Backup update")
    (git/with-credentials {:login config/dbbackup-username :pw config/dbbackup-private-key}
      (git/git-push repo "origin" "main"))
    (println "Backup pushed successfully.")))

(defn write-at-shutdown []
  (.addShutdownHook (Runtime/getRuntime)
    (new Thread
      (fn []
        (write))))
        
  (println "Shutdown handler started, waiting for SIGTERM...")
  (let [lock (new Object)]
    (locking lock
      (.wait lock))))

(defn -main [& _]
  (if-not config/dbbackup-remote 
    (println "`dbbackup-remote` not set in config, skipping backup write.")
    ;; else
    (do (write-at-shutdown)
        (println "Successfully handled shutdown."))))







