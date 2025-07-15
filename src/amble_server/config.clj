(ns amble-server.config
  (:require
   [environ.core :refer [env]]))

(def client-url
  (env :client-url))

(def port
  (env :port))
