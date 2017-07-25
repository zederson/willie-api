(ns willie-api.collector)

(defprotocol Collector
  (service-type [this])
  (collect [this username]))

