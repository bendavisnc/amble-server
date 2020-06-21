(ns amble-server.depot.game
  (:require [clojure.java.shell :as shell]
            [clojure.edn :as edn])
  (:import (java.util.regex Pattern)))

(def target-dir "../.amble-gamedepot")

(defn find [game-id]
  (let [branch-list
        (shell/sh "git" "branch" "-l" :dir target-dir)
        branches
        (clojure.string/split (:out branch-list)
                              (Pattern/compile "\\s"))
        branch
        (filter
                (fn [branch-name-listed]
                  (= branch-name-listed game-id))
                branches)]
    (and (first branch)
         (edn/read-string (slurp (str target-dir
                                      "/"
                                      "game-base.json"))))))


(defn create!
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

(defn delete!
  "Deletes the branch with the given game id."
  [game-id]
  (let [branch-delete
        (shell/sh "git" "branch" "-d" game-id :dir target-dir)]
    (assert (clojure.string/includes? (:out branch-delete)
                                      (str "Deleted branch " game-id)))
    game-id))
