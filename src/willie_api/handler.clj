(ns willie-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [willie-api.environment :as env]
            [willie-api.services.collector-service :as collector-service]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]))

(defroutes app-routes
  (GET "/heart_beat" [] {:status 200 :headers {"Content-Type" "application/json"} :body { :status :ok }})
  (GET "/collect/:username" [username] {:status 200 :headers {"Content-Type" "application/json"} :body (collector-service/collect username) })
  (route/not-found "Willie Not Found"))

(def app
  (-> app-routes
      wrap-json-response
      wrap-json-body))

(env/load-env)
