(ns amble-server.move-async
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.move-async"))

(def websockets-path "/move/async")

(defn on-connect [ws & args]
  (.info log "New move async connection!")
  (.debug log args)
  nil)

(defn on-error [ws & args]
  (.info log "New move async error")
  (.debug log args)
  nil)


(def handlers {:on-connect on-connect
               :on-error on-error 
               :on-text nil
               :on-close nil
               :on-bytes nil})

