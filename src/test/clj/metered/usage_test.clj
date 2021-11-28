(ns metered.usage-test
  (:require
   [clojure.test :refer [deftest is are testing run-tests]]
   [metered.usage :as cut]))

(def events
  [{:usage/event     :usage.event/create
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-03-10T00:00:00.000-00:00"}
   {:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/object
    :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-03-10T01:00:00.000-00:00"}])

(deftest should-generate-secret
  (is
   (= [{:usage/resource  :usage.resource/object
        :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
        :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
        :usage/duration  60}]
      (cut/process-usage events))))