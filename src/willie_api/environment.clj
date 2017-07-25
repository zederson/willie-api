(ns willie-api.environment
  (:require [clojure.java.io :as io]))

(def ^:private default-file-name ".lein-env")
(def loaded? (atom false))

(defn load-env
  [& [file-name]]
  (let [path-file (or file-name default-file-name)]
    (println "Load environment variables from " path-file)
    (with-open [reader (io/reader path-file)]
      (let [props (java.util.Properties.)]
        (.load props reader)
        (doseq [[k v] props]
          (System/setProperty k v)))
        (println "Loaded environment variables")
        (swap! loaded? (fn[n] true))
        )))

(defn env
  [attribute]
  (or (System/getenv attribute) (System/getProperty attribute)))
