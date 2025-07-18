(ns amble-server.config
  (:require
   [environ.core :refer [env]]))

(def client-url
  (env :client-url))

(def port
  (env :port))

(def postgres?
  (= "true"
     (env :postgres)))

(def postgres-subname
  (env :postgres-subname))

(def postgres-username
  (env :postgres-username))

(def postgres-password
  (env :postgres-password))
