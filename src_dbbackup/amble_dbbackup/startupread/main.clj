(ns amble-dbbackup.startupread.main
  (:require
   [amble-dbbackup.config :as config]
   [com.twinql.clojure.git :as git])
  (:gen-class)) 

(defn read []
  (assert config/dbbackup-remote "`dbbackup-remote` not set in config.")
  (println (git/clone config/dbbackup-remote)))

(defn -main [& _]
  (read)
  (println "Database backup read successfully."))


