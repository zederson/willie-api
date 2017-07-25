(ns willie-api.collectors.github
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [willie-api.environment :as env]
            [willie-api.collector :as collect]))

(def ^:private url-user "https://api.github.com/search/users?q=")
(def ^:private url-repositories "https://api.github.com/users/%s/repos")
(def ^:private url-stats-commit "https://api.github.com/repos/%s/%s/stats/commit_activity")
(def ^:private headers {"Authorization" (format " token %s" (env/env "GITHUB_TOKEN")) })

(defn get-data
  [url]
  (println "GET " url)
    (let [response (client/get url {:accept :json :throw-entire-message? true :headers headers})]
      (json/read-str (:body response) :key-fn keyword)))

(defn search-user
  "Search last user"
  [user]
  (let [body (get-data (str url-user user))]
    (->> body :items last)))

(defn load-repositories
  [username]
  (get-data (format url-repositories username)))

(defn total-commits
  "Get the last year of commit activity data"
  [username repository]
  (let [body (get-data (format url-stats-commit username repository))]
    (->> body
         (#(map :total %))
         (reduce +))))

(defn build-hash-from-repository
  [username item]
   { :id (:id item)
     :name (:name item)
     :full_name (:full_name item)
     :url (:url item)
     :description (:description item)
     :created_at (:created_at item)
     :language (:language item)
     :fork (:fork item)
     :last_year_commits (total-commits username (:name item)) })

(defn load-user-data
  [username]
  (let [user (search-user username)
        login (:login user)
        repos (load-repositories login)]
    (let [result-repositories (->> repos (map (fn[item] (build-hash-from-repository login item))))]
      {
       :id (:id user)
       :login login
       :avatar_url (:avatar_url user)
       :total_last_year_commits (->> result-repositories (#(map :last_year_commits %)) (reduce +))
       :repositories (vec result-repositories)
      } )))

(defrecord GithubCollector[]
  collect/Collector
  (service-type [this] :github )
  (collect
    [this username]
    (load-user-data username)))
