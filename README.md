## 1. A simple metered billing system

### Objective

We define a simple billing system which feeds off a list of usage records.
Usage records designate events happening on resources. Each usage record
is represented as a map of the following form:

```clojure
{:usage/event     :usage.event/create
 :usage/resource  :usage.resource/object
 :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
 :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
 :usage/timestamp #inst "2017-03-10T00:00:00.000-00:00"}
```

- `:usage/event` may be any of `:usage.event/create` or
  `:usage.event/destroy`.
- `:usage/resource` may be any of `:usage.resource/vm` or
  `:usage.resource/object`.
- `:usage/uuid` designates the resource the event pertains to.
- `:usage/account` designates the account owning the designated
  resource.
- `:usage/timestamp` designates the time at which the event occurred.

Write a function `process-usage` which computes a collection of usage
records and produces a collection of billing statements, one per
account:

```clojure
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

(assert (= (process-usage events)
           [{:usage/resource  :usage.resource/object
             :usage/uuid      #uuid "d8377d93-db71-488a-b894-54a962760bea"
             :usage/account   #uuid "ee12577c-983f-4729-a0e9-c5789a906c04"
             :usage/duration  60}]))
```

### Assignment details

Please write the code in a single `.clj` file which pulls-in no
additional dependencies beyond `[clj-time "0.13.0"]` if necessary.
The namespace should be named `metered.usage`.

Please respect function names as a test-suite will be ran on the