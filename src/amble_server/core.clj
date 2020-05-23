(ns amble-server.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app
           (POST "/game" [] "<h1>Hello World</h1>"))

(defn -main [& args]
  (println "helloooooo!!!!"))