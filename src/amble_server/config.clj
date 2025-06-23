(ns amble-server.config
  (:require [environ.core :refer [env]]))

(def port
  (env :port))