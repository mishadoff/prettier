(ns prettier.time-test
  (:require [clojure.test :refer :all]
            [prettier.time :as time]))

(deftest interval->readable--defaults-test
  (is (= "123 milliseconds" (time/ms->readable 123)))
  (is (= "2 seconds 100 milliseconds" (time/ms->readable 2100)))
  (is (= "5 minutes" (time/ms->readable 300000)))
  (is (= "4 hours 10 minutes 200 milliseconds" (time/ms->readable 15000200)))
  (is (= "11 days 8 hours 13 minutes 20 seconds" (time/ms->readable 980000000)))
  (is (= "3 months 25 days 17 hours 46 minutes 40 seconds" (time/ms->readable 10000000000)))
  (is (= "32 years 1 months 24 days 1 hours 46 minutes 40 seconds" (time/ms->readable 1000000000000))))

(deftest interval->readable--single-unit-test
  (binding [time/*time-units* {:hours "hours"}]
    (is (= "79.5 hours" (time/ms->readable 286217846)))
    (is (= "0.0 hours" (time/ms->readable 100000))))
  (binding [time/*time-units* {:years "ys"}
            time/*time-decimal-format* "%.6f"]
    (is (= "0.000032 ys" (time/ms->readable 1000000)))
    (is (= "321.502058 ys" (time/ms->readable 10000000000000)))))

(deftest interval->readable--errors-test
  (is (thrown? AssertionError (time/ms->readable "time")))
  (is (thrown? AssertionError (time/ms->readable {:map 1})))
  (is (thrown? AssertionError (time/ms->readable 0)))
  (is (thrown? AssertionError (time/ms->readable -10000))))

(deftest interval->readable--custom-labels-test
  (binding [time/*time-units* {:seconds      "s"
                               :minutes      "m"
                               :hours        "h"
                               :days         "d"}
            time/*time-unit-gap* ""
            time/*time-unit-separator* ""]
    (is (= "1m40s" (time/ms->readable 100000)))
    (is (= "2h28m54s" (time/ms->readable 8934751)))
    (is (= "8d11h32m36s" (time/ms->readable 732756812)))))

(deftest interval->readable--custom-time-hierarchy-test
  (binding [time/*time-hierarchy* [[:days (* 1000 60 60 24)]
                                   [:weeks 7]
                                   [:months 4]]
            time/*time-units* {:days "DAYS" :weeks "WEEKS" :months "MONTHS"}]
    (is (= "2 MONTHS 1 WEEKS 5 DAYS" (time/ms->readable 5900000000)))
    (is (= "3 WEEKS 2 DAYS" (time/ms->readable 2000000000)))))

(deftest interval->readable--invalid-time-units
  (binding [time/*time-units* {}]
    (is (thrown? AssertionError (time/ms->readable 1)))))