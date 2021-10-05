(ns amble-nonsense.main)

(defn -main [& args]
  (println
    (map (fn [x]
           (mapcat (fn [y]
                     [x, y, 7])
                   [4, 6]))
            ;[])))
         [1 2 3])))
;(for [x [1, 2]]
     ;  (for [y [4, 6]]
