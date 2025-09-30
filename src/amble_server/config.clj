(ns amble-server.config
  (:require
   [environ.core :as environ]))

(defmacro defenv [name key]
  `(def ~name ~(environ/env key)))

(defenv client-url :client-url)
(defenv port :port)
(defenv sqlite-db :sqlite-db)
(defenv postgres-subname :postgres-subname)
(defenv postgres-username :postgres-username)
(defenv postgres-password :postgres-password)
(defenv dev-env :env)

;; Derived values
(def postgres?
  (and postgres-subname
       postgres-username
       postgres-password))

(def devmode?
  (= dev-env "dev"))
