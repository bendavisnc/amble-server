(ns amble-shutdown.main
  (:gen-class))

(defn -main [& _]
  (.addShutdownHook (Runtime/getRuntime)
    (new Thread
      (fn []
        (println "Handling amble shutdown."))))
  (println "Shutdown handler started, waiting for SIGTERM...")
  (let [lock (new Object)]
    (locking lock
      (.wait lock))))


