(ns amble-server.config
  (:require
   [environ.core :refer [env]]))

;; Defines a lazy var that evaluates once at first access, then stores the value
(defmacro deflazy [name expr]
  `(def ~name (let [d# (delay ~expr)] @d#)))

(deflazy client-url
  (env :client-url))

(deflazy port
  (env :port))

(deflazy postgres-subname
  (env :postgres-subname))

(deflazy postgres-username
  (env :postgres-username))

(deflazy postgres-password
  (env :postgres-password))

(deflazy postgres?
  (and postgres-subname
       postgres-username
       postgres-password))

(deflazy devmode?
  (= (env :env) "dev"))