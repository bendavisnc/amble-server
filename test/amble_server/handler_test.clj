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
             (json/read-str (:body get-game-response) :key-fn keyword)))))


  (testing "get players"
    (let [get-players-request  (mock-request/request
                                :get
                                (str "/game/" test-game-id "/player"))
          get-players-response (main/handler get-players-request)]
      (is (= 200
             (:status get-players-response)))
      (is (= ["player-one" "player-two" "player-three" "player-four" "player-five" "player-six"]
             (json/read-str (:body get-players-response)))))) 

  (testing "get player"
    (let [get-player-request  (mock-request/request
                                :get
                                (str "/game/" test-game-id "/player/player-one"))
          get-player-response (main/handler get-player-request)]
      (is (= 200
             (:status get-player-response)))
      (is (= [[0.425 0.7165] [0.475 0.7165] [0.525 0.7165] [0.575 0.7165] [0.45 0.7598] [0.5 0.7598] [0.55 0.7598] [0.475 0.8031] [0.525 0.8031] [0.5 0.8464]]
             (json/read-str (:body get-player-response)))))) 

  (testing "get player (after made move)"
    (let [_ (main/handler
             (-> (mock-request/request :post (str "/game/" test-game-id "/player/player-one/move/0"))
                 (mock-request/json-body {:game-id test-game-id
                                          :x 0.5
                                          :y 0.5})))

          get-player-request  (mock-request/request
                                :get
                                (str "/game/" test-game-id "/player/player-one"))
          get-player-response (main/handler get-player-request)]
      (is (= 200
             (:status get-player-response)))
      (is (= [[0.5 0.5] [0.475 0.7165] [0.525 0.7165] [0.575 0.7165] [0.45 0.7598] [0.5 0.7598] [0.55 0.7598] [0.475 0.8031] [0.525 0.8031] [0.5 0.8464]]
             (json/read-str (:body get-player-response))))))) 















