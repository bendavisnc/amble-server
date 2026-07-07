(ns amble-server.api.board
  (:refer-clojure :exclude [get])
  (:require
    [amble-server.resource.game :as game-resource]
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [ring.util.response :as response-util]))

(defn get
  "Returns either a not found, an error, or a constant value'd response representing the piece coordinates of a chinese checkers set."
  [req]
  (let [game-id (keyword (:game-id (:params req)))]
    (try
      (if (game-resource/get game-id)
        (response-util/response
         (edn/read-string (slurp (io/resource "board.json"))))
        (response-util/status req 404))
      (catch Throwable e
        (throw (ex-info "An error occurred during game board get."
                        {:game-id game-id}
                        e))))))


