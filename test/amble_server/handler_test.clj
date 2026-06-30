(ns amble-server.handler-test
  (:require [amble-server.api.handler :as api-handler]
            [clojure.data.json :as json]
            [clojure.test :refer :all]
            [ring.mock.request :as mock-request]))

(def test-game-id "TheTestGame")

(deftest test-amble-server-api
  (testing "create game"
    (let [
          _ (api-handler/handler
             (mock-request/request :delete (str "/game/" test-game-id)))

          create-game-request  (mock-request/request :post
                                                     "/game"
                                                     (json/write-str
                                                      {:game-id test-game-id}))
          create-game-response (api-handler/handler create-game-request)]
      (is (= 201
             (:status create-game-response)))
      (is (= {:game-id test-game-id}
             (json/read-str (:body create-game-response) :key-fn keyword)))))

  (testing "get game"
    (let [get-game-request  (mock-request/request :get
                                                  (str "/game/" test-game-id))
          get-game-response (api-handler/handler get-game-request)]
      (is (= 200
             (:status get-game-response)))
      (is (= {:game-id test-game-id}
             (json/read-str (:body get-game-response) :key-fn keyword))))))

