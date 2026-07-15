(ns amble-dbbackup.shutdownwrite.main
  (:gen-class)
  (:require
    [amble-dbbackup.config :as config]
    [clj-jgit.porcelain :as git]))

(defn write
  []
  (let [repo (git/load-repo config/dbbackup)]
    (git/git-add repo ".")
    (git/git-commit repo "Backup update")
    (git/with-credentials {:login config/dbbackup-username
                           :pw    config/dbbackup-private-key}
                          (git/git-push repo "origin" "main"))))

(defn write-at-shutdown
  []
  (.addShutdownHook (Runtime/getRuntime)
                    (new Thread
                         (fn []
                           (try 
                            (println "Backup push initiated.")
                            (write)
                            (println "Backup pushed successfully.")
                            (catch Throwable e
                              (throw (ex-info "Something bad happened while trying to push backup during shutdown." {} e)))))))

  (println "Shutdown handler started, waiting for SIGTERM...")
  (let [lock (new Object)]
    (locking lock
      (.wait lock))))

(defn -main
  [& _]
  (if-not config/dbbackup-remote
    (println "`dbbackup-remote` not set in config, skipping backup write.")
    ;; else
    (write-at-shutdown)))







