(ns amble-server.config
  (:require
    [environ.core :as environ]))

(def client-url
  (or (environ/env :client-url)
      (throw (new Exception "Missing config, `client-url`"))))

(def port
  (or (environ/env :port)
      (throw (new Exception "Missing config, `port`"))))

(def sqlite-db (environ/env :sqlite-db))

(def postgres-subname (environ/env :postgres-subname))

(def postgres-username (environ/env :postgres-username))

(def postgres-password (environ/env :postgres-password))

(def dev-env (environ/env :dev-env))

;; Derived values
(def postgres?
  (and postgres-subname
       postgres-username
       postgres-password))

(def devmode?
  (= dev-env "dev"))
