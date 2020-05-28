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

(def target-dir ".amble-gamedepot")

(defn git [callback])

;; Creates a new branch  with a name from the given game id.
(defn create [game-id]
  (let [output-sh 
        (:out
        ;  (shell/sh 
                ;   "ls"))]
                ;   "pwd"))]
                ;    (str "mkdir " target-dir)))]
                ;   (str "mkdir " target-dir ";\n" "cd " target-dir "; git status")))]
                ;    "cd " target-dir))]
         (shell/sh "git" "status" :dir target-dir))]

    (println "neat...")
    (println output-sh)
    output-sh))
 
  
 
 
