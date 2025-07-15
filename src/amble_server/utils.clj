(ns amble-server.utils
  (:import
   (java.util Calendar Locale)))

(defn- day-of-week []
  (.getDisplayName (Calendar/getInstance)
                   Calendar/DAY_OF_WEEK
                   Calendar/LONG
                   (Locale/getDefault)))

(defn momentary-game-name []
  (str
    "The"
    (day-of-week)
    "Game"))
