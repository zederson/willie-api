(ns willie-api.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [willie-api.handler :refer :all]))

(defn- parse-json
  [data]
  (json/read-str data :key-fn keyword))

(deftest load-user-data
  (testing "load user data"
    (let [response (app (mock/request :get "/heart_beat"))]
      (is (= (:status response) 200))
      (is (= (parse-json (:body response)) {:status "ok"}))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:body response) "Willie Not Found"))
      (is (= (:status response) 404)))))
