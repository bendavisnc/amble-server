(ns build
  (:require [clojure.tools.build.api :as b]))

(def server-basis (b/create-basis {:project "deps.edn"}))
(def class-dir "target/classes")

(def server-jar "target/server.jar")
(def dbbackupread-jar "target/dbbackupread.jar")
(def dbbackupwrite-jar "target/dbbackupwrite.jar")


(def dbbackup-basis (b/create-basis {:project "deps.edn"
                                     :aliases [:dbbackupapp]}))

(defn server-uber [_]
  (b/copy-dir {:src-dirs ["src" "resources"] :target-dir class-dir})
  (b/compile-clj {:basis server-basis :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file server-jar
           :basis server-basis
           :main 'amble-server.main}))

(defn dbbackupread-uber [_]
  (b/copy-dir {:src-dirs ["src_dbbackup"] :target-dir class-dir})
  (b/compile-clj {:basis dbbackup-basis :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file dbbackupread-jar
           :basis dbbackup-basis
           :main 'amble-dbbackup.startupread.main}))


(defn dbbackupwrite-uber [_]
  (b/copy-dir {:src-dirs ["src_dbbackup"] :target-dir class-dir})
  (b/compile-clj {:basis dbbackup-basis :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file dbbackupwrite-jar
           :basis dbbackup-basis
           :main 'amble-dbbackup.shutdownwrite.main}))
