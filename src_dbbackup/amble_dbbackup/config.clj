(ns amble-dbbackup.config
  (:require
    [environ.core :as environ]))

(defmacro defenv
  [name key]
  `(def ~name ~(environ/env key)))

(defenv dbbackup :dbbackup)
(defenv dbbackup-remote :dbbackup-remote)
(defenv dbbackup-username :dbbackup-username)
(defenv dbbackup-private-key :dbbackup-private-key)
