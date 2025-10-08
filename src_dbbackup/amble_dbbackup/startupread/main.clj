(ns amble-dbbackup.startupread.main
  (:require
   [amble-dbbackup.config :as config]
   [clj-jgit.porcelain :as git])
  (:gen-class)) 

(defn read []
  (assert config/dbbackup-remote "`dbbackup-remote` not set in config.")
  (println (git/git-clone config/dbbackup-remote)))

(defn -main [& _]
  (read)
  (println "Database backup read successfully."))
