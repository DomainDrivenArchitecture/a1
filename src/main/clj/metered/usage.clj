(ns metered.usage
  (:require
   [clojure.spec.alpha :as s]))

(defn event? [input]
  (contains? #{:create :destroy} input))
(defn resource? [input]
  (contains? #{:vm :object} input))
(defn uuid? [input] 
  (instance? java.util.UUID input))
(defn timestamp? [input]
  (instance? java.util.Date input))
(def minute? int?)

(s/def ::event event?)
(s/def ::resource resource?)
(s/def ::uuid uuid?)
(s/def ::account uuid?)
(s/def ::timestamp timestamp?)
(s/def ::usage-record (s/keys :req-un [::event ::resource ::uuid ::account ::timestamp]))

(s/def ::duration minute?)
(s/def ::billing-statement (s/keys :req-un [::resource ::uuid ::account ::duration]))

(defn process-usage [events]
  )