(ns amble-server.handler-test
  (:require
    [amble-server.main :as main]
    [clojure.data.json :as json]
    [clojure.test :refer [deftest is testing]]
    [ring.mock.request :as mock-request]))

(def test-game-id "TheTestGame")

(deftest test-amble-server-api
  (testing "create game"
    (let [_ (main/handler
             (mock-request/request :delete (str "/game/" test-game-id)))
          create-game-request  (-> (mock-request/request :post "/game")
                                   (mock-request/json-body {:game-id
                                                            test-game-id}))
          create-game-response (main/handler create-game-request)]
      (is (= 201
             (:status create-game-response)))
      (is (= {:game-id test-game-id}
             (json/read-str (:body create-game-response) :key-fn keyword)))))

  (testing "get game"
    (let [get-game-request  (mock-request/request :get
                                                  (str "/game/" test-game-id))
          get-game-response (main/handler get-game-request)]
      (is (= 200
             (:status get-game-response)))
      (is (= {:game-id test-game-id}
             (json/read-str (:body get-game-response) :key-fn keyword))))))

