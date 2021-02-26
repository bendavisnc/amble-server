(ns amble-server.resource.game
  (:require
   [amble-server.db.game :as game-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(declare inform)

(def log (. LogManager getLogger "amble-server.resource.game"))

(defn get [id]
  (inform "Getting" id)
  (game-db/find id))

(defn create! [id]
  (inform "Creating", id)
  (game-db/create! id))

(defn delete! [id]
  (inform "Deleting", id)
  (game-db/delete! id))

(defn inform [about, id]
  (.info log (str about
                  " game resource by id, \""
                  id
                  "\".")))

