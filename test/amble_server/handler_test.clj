(ns amble-server.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock-request]
            [amble-server.handler :refer :all]
            [clojure.edn :as edn]
            [clojure.data.json :as json]))

(def test-game-prefix "test-")

(deftest test-amble-server-api
  (testing "create game"
    (let [game-id (:body (app (mock-request/request :get "/game-id")))
          _ (app (mock-request/request :delete
                                       (str "/game/"
                                            test-game-prefix
                                            game-id)))

          create-game-request (-> (mock-request/request :post "/game")
                                  (mock-request/header :x-amble-game-id-prefix test-game-prefix))
          create-game-response (app create-game-request)]
      (is (= 201
             (:status create-game-response)))
      (is (= {"game-id" (str test-game-prefix game-id)}
             (json/read-str (:body create-game-response))))))

  (testing "get game"
    (let [game-id (:body (app (mock-request/request :get "/game-id")))
          get-game-request (-> (mock-request/request :get
                                                     (str "/game/"
                                                          test-game-prefix
                                                          game-id)))
          get-game-response (app get-game-request)]
      (is (= 200
             (:status get-game-response)))
      (is (= [[1 2] [3 4]]
             (json/read-str (:body get-game-response)))))))

