(ns willie-api.collectors.github-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [willie-api.collectors.github :refer :all]))

(defn- read-file
  [file]
  (let [data (slurp (format "test/resources/collectors/github/%s.json" file))]
    (json/read-str data :key-fn keyword)))

(def USERNAME "zederson" )
(def REPOSITORY "frajola" )

(deftest test-search-user
  (testing "search user from github"
    (with-redefs [get-data (fn[url] (read-file "user") )]
      (is (= (search-user USERNAME) {:html_url "https://github.com/zederson", :gravatar_id "", :followers_url "https://api.github.com/users/zederson/followers", :subscriptions_url "https://api.github.com/users/zederson/subscriptions", :site_admin false, :following_url "https://api.github.com/users/zederson/following{/other_user}", :type "User", :received_events_url "https://api.github.com/users/zederson/received_events", :login "zederson", :organizations_url "https://api.github.com/users/zederson/orgs", :id 4307805, :score 64.74985, :events_url "https://api.github.com/users/zederson/events{/privacy}", :url "https://api.github.com/users/zederson", :repos_url "https://api.github.com/users/zederson/repos", :starred_url "https://api.github.com/users/zederson/starred{/owner}{/repo}", :gists_url "https://api.github.com/users/zederson/gists{/gist_id}", :avatar_url "https://avatars0.githubusercontent.com/u/4307805?v=4"} ))
    )))

(deftest test-load-repositories
  (testing "serach repositories from github"
    (with-redefs [get-data (fn [url] (read-file "repos"))]
      (is (not (empty? (load-repositories USERNAME))))
    )))

(deftest test-total-commits
  (testing "calculate total commits"
    (with-redefs [get-data (fn[user] (read-file "commit_activity") )]
      (is (= (total-commits USERNAME REPOSITORY) 40))
    )))

(deftest test-build-hash
  (testing "build hash from repository"
    (with-redefs [total-commits (fn[user repo] 40)]
      (let [repository (first (read-file "repos"))
            result (build-hash-from-repository USERNAME repository)]
        (is (= result {
                       :description "Cadastros de Ampolas",
                       :name "ampolas",
                       :language "JavaScript",
                       :id 80546697,
                       :url "https://api.github.com/repos/zederson/ampolas",
                       :full_name "zederson/ampolas",
                       :fork false,
                       :last_year_commits 40,
                       :created_at "2017-01-31T18:04:06Z"})) ))))

(deftest test-load-user-data
  (testing "load data from user"
    (with-redefs [get-data (fn[username] (read-file "user"))
                  total-commits (fn[username repo] 40)
                  load-repositories (fn[username] (read-file "repos") )]
      (let [result (load-user-data USERNAME)]
        (is (= (:id result) 4307805))
        (is (= (:avatar_url result) "https://avatars0.githubusercontent.com/u/4307805?v=4"))
        (is (= (:total_last_year_commits result) 120))
        (is (not (empty? (:repositories result))))
        (is (= (count (:repositories result)) 3))
      ))))


