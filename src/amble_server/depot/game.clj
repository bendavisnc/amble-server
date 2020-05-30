(ns amble-server.depot.game
  (:require [clojure.java.shell :as shell]))

; (defn get-by-id [req]
;   (println "Sending get game by id response.")
;   (let [game-id "fortyTwo"]
;     (-> (response/render game-id req)
;         (response-util/status 200)
;         (response-util/content-type "application/json"))))


; (defn get-by-tag [req]
;   (println "Sending get game by tag response.")
;   (let [games-found ["immaPretendGameBranchName"]]
;     (-> (response/render (str games-found) req)
;         (response-util/status 200)
;         (response-util/content-type "application/json"))))

(def target-dir "../.amble-gamedepot")

;; Creates a new branch with a name from the given game id.
(defn create [game-id]
  (let [branch-create
        (shell/sh "git" "checkout" "-b" game-id :dir target-dir)
        branch-checkout-prior
        (shell/sh "git" "checkout" "-" :dir target-dir)]
    (println (:err branch-create))
    (println (str "Switched to a new branch '" game-id "'"))
    (assert (= branch-create
               {:exit 0, :out "", :err (str "Switched to a new branch '" game-id "'/n")}))
    (assert (= branch-checkout-prior)
           {:exit 0, :out "", :err "Switched to branch 'amble-game-base'"})
    (:out branch-create)))





