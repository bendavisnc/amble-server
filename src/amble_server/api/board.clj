(ns amble-server.api.board
  (:require
   [amble-server.resource.game :as game-resource]
   [amble-server.utils :as utils]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [ring.util.response :as response-util])
  (:import
   (org.apache.logging.log4j LogManager Logger)))

(def log (. LogManager getLogger "amble-server.api.board"))

(defn get
  "Returns either a not found, an error, or a constant value'd response representing the piece coordinates of a chinese checkers set."
  [req]
  (let [game-id (keyword (:game-id (:params req)))
        found (game-resource/get game-id)]
    (try
      (cond (not found)
            (response-util/status req 404)
            :default
            ; (response-util/response (slurp (io/resource "board.json")))
            (response-util/response
              (edn/read-string (slurp (io/resource "board.json")))))
      (catch Throwable e
        (.error log "An error occurred during game board get.")
        (.error log e)
        (response-util/status req 500)))))
