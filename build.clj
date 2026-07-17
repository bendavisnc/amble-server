(ns build
  (:require [clojure.tools.build.api :as b]))

(def server-basis (b/create-basis {:project "deps.edn"}))
(def class-dir "target/classes")

(def server-jar "target/server.jar")

(defn server-uber
  [_]
  (b/copy-dir {:src-dirs ["src" "resources"] :target-dir class-dir})
  (b/compile-clj {:basis server-basis :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file server-jar
           :basis     server-basis
           :main      'amble-server.main}))
