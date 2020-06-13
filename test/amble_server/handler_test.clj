(ns amble-server.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock-request]
            [amble-server.handler :refer :all]))

(def test-game-prefix "test-")

(deftest test-amble-server-api
  (testing "game create"
    (let [game-id (:body (app (mock-request/request :get "/game-id")))
          _ (app (mock-request/request :delete
                                       (str "/game/"
                                            test-game-prefix
                                            game-id)))

          create-game-request (-> (mock-request/request :post "/game")
                                  (mock-request/header :x-amble-game-id-prefix test-game-prefix))
          create-game-response (app create-game-request)]
      (is (= 201
             (:status create-game-response))) ;; Create game works.
      (let [id-game-created (:body create-game-response)
            get-game-response (app (mock-request/request :get
                                                         (str "/game"
                                                              id-game-created)))]
        (println "banana")
        (println id-game-created)
        (is (= (:status get-game-response) 200))))))
