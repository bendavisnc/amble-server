(ns amble-dbbackup.config
  (:require
    [environ.core :as environ]))

(def dbbackup
  (or (environ/env :dbbackup)
      (throw (new Exception "Missing config, `dbbackup`"))))


(def dbbackup-remote
  (or (environ/env :dbbackup-remote)
      (throw (new Exception "Missing config, `dbbackup-remote`"))))


(def dbbackup-username
  (or (environ/env :dbbackup-username)
      (throw (new Exception "Missing config, `dbbackup-username`"))))


(def dbbackup-private-key
  (or (environ/env :dbbackup-private-key)
      (throw (new Exception "Missing config, `dbbackup-private-key`"))))























