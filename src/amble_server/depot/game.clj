(ns amble-server.depot.game
  (:require [clojure.java.shell :as shell])
  (:import (java.util.regex Pattern)))

(def target-dir "../.amble-gamedepot")

(defn find-by-id []
  nil)


(defn find-by-tag [tag]
  (let [branch-list
        (shell/sh "git" "branch" "-l" :dir target-dir)
        branches
        (clojure.string/split (:out branch-list)
                              (Pattern/compile "\\s"))]
    (cond (some (fn [branch-name]
                  (= tag branch-name))
                branches)
          (shell/sh "git" "show" "--oneline" tag :dir target-dir)
          :default
          nil)))



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
