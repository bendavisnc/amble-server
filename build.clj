(ns build
  (:require [clojure.tools.build.api :as b]))

(def server-basis (b/create-basis {:project "deps.edn"}))
(def class-dir "target/classes")

(def server-jar "target/server.jar")
(def shutdown-jar "target/shutdown.jar")


(def shutdownapp-basis (b/create-basis {:project "deps.edn"
                                        :aliases [:shutdownapp]}))

(defn server-uber [_]
  (b/copy-dir {:src-dirs ["src" "resources"] :target-dir class-dir})
  (b/compile-clj {:basis server-basis :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file server-jar
           :basis server-basis
           :main 'amble-server.main}))

(defn shutdown-uber [_]
  (b/copy-dir {:src-dirs ["src_amble_shutdown"] :target-dir class-dir})
  (b/compile-clj {:basis shutdownapp-basis :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file shutdown-jar
           :basis shutdownapp-basis
           :main 'amble-shutdown.main}))
