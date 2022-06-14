(ns amble-server.db.core
  (:require
            [clojure.java.jdbc :as jdbc] 
            [clojure.java.io :as io])
  (:import [java.sql DriverManager]))


(def target-db-path "../.amble-db/amble.db")

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     target-db-path
   :foreign_keys "on"})

;; ;; (def global-connection (jdbc/get-connection db))
;; (defn global-connection [& args]
;;   (let [conn (jdbc/get-connection db)]
;;     (when-let [listener]  
;;       (.addUpdateListener conn listener))   
;;     conn)) 
