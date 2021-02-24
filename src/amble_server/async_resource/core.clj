(ns amble-server.async-resource.core
  (:require [amble-server.async-resource.move :as async-move]))

(defn on-connect [& args]
  (println "on-connect")
  (println args)
  (async-move/on-connect! nil, nil, nil))

(def handler-fns {:on-connnect on-connect})
                  ;:on-error on-error
                  ;:on-text on-text
                  ;:on-close on-close
                  ;:on-bytes on-bytes



;(def websockets-handler
;  {:on-connect on-connect
;   :on-error on-error
;   :on-text on-text
;   :on-close on-close
;   :on-bytes on-bytes})
;


