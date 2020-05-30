(ns amble-server.depot.game
  (:require [clojure.java.shell :as shell]))

(def target-dir "../.amble-gamedepot")

(defn find-by-id []
  nil)


(defn find-by-tag []
  nil)


(defn create
  "Creates a new branch with a name from the given game id."
  [game-id]
  (let [branch-create
        (shell/sh "git" "checkout" "-b" game-id :dir target-dir)
        branch-checkout-prior
        (shell/sh "git" "checkout" "-" :dir target-dir)]
    (assert (= branch-create
               {:exit 0, :out "", :err (str "Switched to a new branch '" game-id "'\n")}))
    (assert (= branch-checkout-prior)
            {:exit 0, :out "", :err "Switched to branch 'amble-game-base'\n"})
    game-id))
