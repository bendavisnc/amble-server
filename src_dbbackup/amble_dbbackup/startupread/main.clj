(ns amble-dbbackup.startupread.main
  (:require
    [amble-dbbackup.config :as config]
    [clj-jgit.porcelain :as git])
  (:gen-class)) 

(defn read []
  (assert config/dbbackup-remote "`dbbackup-remote` not set in config.")
  (assert config/dbbackup-private-key "`dbbackup-private-key` not set in config.")
  (git/with-credentials {:login config/dbbackup-username :pw config/dbbackup-private-key}
    (git/git-clone config/dbbackup-remote :branch "main")))

(defn -main [& _]
  (read)
  (println "Database backup read successfully."))



