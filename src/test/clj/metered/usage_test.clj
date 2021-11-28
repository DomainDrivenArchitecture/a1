(ns metered.usage-test
  (:require
   [clojure.test :refer :all]
   [data-test :refer :all]
   [clojure.spec.alpha :as s]
   [clojure.spec.test.alpha :as stest]
   [metered.usage :as cut]))

(stest/instrument `cut/process-usage)
(stest/instrument `cut/process-usage-at-time)
(stest/instrument `cut/calculate-billing)
(stest/instrument `cut/calculate-minutes-at-time)

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

(def invalid-events
  [{:usage/event     :usage.event/destroy
    :usage/resource  :usage.resource/invalid
    :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
    :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
    :usage/timestamp #inst "2017-03-10T01:00:00.000-00:00"}])

(deftest should-throw-exeption-on-wrong-input
  (is
   (thrown? clojure.lang.ExceptionInfo
            (cut/process-usage invalid-events))))

(deftest should-calculate-billing
  (is
   (= []
      (cut/process-usage [])))
  (is
   (= [{:usage/resource  :usage.resource/object
        :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
        :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
        :usage/duration  60}]
      (cut/process-usage events))))

(defdatatest should-calculate-billing-on-more-cases [input expected]
  (is
   (= expected
      (cut/process-usage-at-time (:events input) (:evaluation-time input)))))
