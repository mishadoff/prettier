(ns prettier.time-test
  (:require [clojure.test :refer :all]
            [prettier.time :as time]))

(deftest interval->readable--defaults-test
  (is (= "123 milliseconds" (time/interval->readable 123)))
  (is (= "2 seconds 100 milliseconds" (time/interval->readable 2100)))
  (is (= "5 minutes" (time/interval->readable 300000)))
  (is (= "4 hours 10 minutes 200 milliseconds" (time/interval->readable 15000200)))
  (is (= "11 days 8 hours 13 minutes 20 seconds" (time/interval->readable 980000000)))
  (is (= "3 months 25 days 17 hours 46 minutes 40 seconds" (time/interval->readable 10000000000)))
  (is (= "32 years 1 months 24 days 1 hours 46 minutes 40 seconds" (time/interval->readable 1000000000000))))

(deftest interval->readable--single-unit-test
  (binding [time/*time-units* {:hours "hours"}]
    (is (= "79.5 hours" (time/interval->readable 286217846)))
    (is (= "0.0 hours" (time/interval->readable 100000))))
  (binding [time/*time-units* {:years "ys"}
            time/*time-decimal-format* "%.6f"]
    (is (= "0.000032 ys" (time/interval->readable 1000000)))
    (is (= "321.502058 ys" (time/interval->readable 10000000000000)))))

(deftest interval->readable--errors-test
  (is (thrown? AssertionError (time/interval->readable "time")))
  (is (thrown? AssertionError (time/interval->readable {:map 1})))
  (is (thrown? AssertionError (time/interval->readable 0)))
  (is (thrown? AssertionError (time/interval->readable -10000))))

(deftest interval->readable--custom-labels-test
  (binding [time/*time-units* {:seconds      "s"
                               :minutes      "m"
                               :hours        "h"
                               :days         "d"}
            time/*time-unit-gap* ""
            time/*time-unit-separator* ""]
    (is (= "1m40s" (time/interval->readable 100000)))
    (is (= "2h28m54s" (time/interval->readable 8934751)))
    (is (= "8d11h32m36s" (time/interval->readable 732756812)))))

(deftest interval->readable--custom-time-hierarchy-test
  (binding [time/*time-hierarchy* [[:days (* 1000 60 60 24)]
                                   [:weeks 7]
                                   [:months 4]]
            time/*time-units* {:days "DAYS" :weeks "WEEKS" :months "MONTHS"}]
    (is (= "2 MONTHS 1 WEEKS 5 DAYS" (time/interval->readable 5900000000)))
    (is (= "3 WEEKS 2 DAYS" (time/interval->readable 2000000000)))))