(ns willie-api.services.collector-service
  (:require [willie-api.collectors.github :as github])
  (:import [willie_api.collectors.github GithubCollector]))

(defn build-item
  [collector username]
  {:type (.service-type collector) :data (.collect collector username)})

(def github-obj (willie_api.collectors.github.GithubCollector.))

(def values [github-obj] )

(defn collect
  [username]
  (->> values
       (map (fn [obj] (build-item obj username))) ))
