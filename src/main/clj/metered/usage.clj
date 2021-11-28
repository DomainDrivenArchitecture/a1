(ns metered.usage
  (:require
   [clojure.spec.alpha :as s]))

(defn event? [input]
  (contains? #{:usage.event/create :usage.event/destroy} input))
(defn resource? [input]
  (contains? #{:usage.resource/vm :usage.resource/object} input))
(defn timestamp? [input]
  (instance? java.util.Date input))
(def minute? int?)

(s/def :usage/event event?)
(s/def :usage/resource resource?)
(s/def :usage/uuid uuid?)
(s/def :usage/account uuid?)
(s/def :usage/timestamp timestamp?)
(s/def :usage/usage-record (s/keys :req [:usage/event :usage/resource :usage/uuid :usage/account :usage/timestamp]))
(def usage-records? (s/coll-of :usage/usage-record))
(s/def :usage/usage-records usage-records?)

(s/def :usage/duration minute?)
(s/def :usage/billing-statement (s/keys :req [:usage/resource :usage/uuid :usage/account :usage/duration]))
(def billing-statements? (s/coll-of :usage/billing-statement))
(s/def :usage/billing-statements billing-statements?)


(s/fdef calculate-minutes-at-time
  :ret pos?)
(defn calculate-minutes-at-time [evaluation-time create destroy]
  (let [billing-end (if destroy
                      (:usage/timestamp destroy)
                      evaluation-time)
        billing-start (:usage/timestamp create)]
    (/ (- (.getTime billing-end) (.getTime billing-start)) 60000)))


(s/fdef calculate-billing
  :args #(let [[evaluation-time events] %]
           (s/and (s/valid? usage-records? events)
                  (s/valid? timestamp? evaluation-time)
                  (<= (count events) 2)
                  (>= (count events) 1)))
  :ret billing-statements?)
(defn calculate-billing [evaluation-time events]
  (let [[create destroy] events]
    {:usage/resource  (:usage/resource create)
     :usage/uuid      (:usage/uuid create)
     :usage/account   (:usage/account create)
     :usage/duration  (calculate-minutes-at-time evaluation-time create destroy)})
  )


(s/fdef process-usage-at-time
  :args #(let [[events evaluation-time] %]
           (s/and (s/valid? usage-records? events)
                  (s/valid? timestamp? evaluation-time)))
  :ret billing-statements?)
(defn process-usage-at-time [events evaluation-time]
  (->>
   (group-by :usage/uuid events)
   (map #(calculate-billing evaluation-time (val %)))))


(s/fdef process-usage
  :args #(apply (partial s/valid? usage-records?) %)
  :ret billing-statements?)
(defn process-usage [events]
  (process-usage-at-time events (java.util.Date.)))
