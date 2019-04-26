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

(deftest ms->readable--single-unit-test
  (binding [time/*time-units* {:hours "hours"}]
    (is (= "79.5 hours" (time/ms->readable 286217846)))
    (is (= "0.0 hours" (time/ms->readable 100000))))
  (binding [time/*time-units* {:years "ys"}
            time/*time-decimal-format* "%.6f"]
    (is (= "0.000032 ys" (time/ms->readable 1000000)))
    (is (= "321.502058 ys" (time/ms->readable 10000000000000)))))

(deftest ms->readable--errors-test
  (is (thrown? AssertionError (time/ms->readable "time")))
  (is (thrown? AssertionError (time/ms->readable {:map 1})))
  (is (thrown? AssertionError (time/ms->readable 0)))
  (is (thrown? AssertionError (time/ms->readable -10000))))

(deftest ms->readable--custom-labels-test
  (binding [time/*time-units* {:seconds      "s"
                               :minutes      "m"
                               :hours        "h"
                               :days         "d"}
            time/*time-unit-gap* ""
            time/*time-unit-separator* ""]
    (is (= "1m40s" (time/ms->readable 100000)))
    (is (= "2h28m54s" (time/ms->readable 8934751)))
    (is (= "8d11h32m36s" (time/ms->readable 732756812)))))

(deftest ms->readable--custom-time-hierarchy-test
  (binding [time/*time-hierarchy* [[:days (* 1000 60 60 24)]
                                   [:weeks 7]
                                   [:months 4]]
            time/*time-units* {:days "DAYS" :weeks "WEEKS" :months "MONTHS"}]
    (is (= "2 MONTHS 1 WEEKS 5 DAYS" (time/ms->readable 5900000000)))
    (is (= "3 WEEKS 2 DAYS" (time/ms->readable 2000000000)))))

(deftest ms->readable--invalid-time-units
  (binding [time/*time-units* {}]
    (is (thrown? AssertionError (time/ms->readable 1)))))

;;;

(deftest readable->ms--success-test
  (is (= 0 (time/readable->ms "0ms")))
  (is (= 132 (time/readable->ms "132 ms")))
  (is (= 5 (time/readable->ms "5 milliseconds")))
  (is (= 30000 (time/readable->ms "30 seconds")))
  (is (= 180000 (time/readable->ms "3m")))
  (is (= 300000 (time/readable->ms "5 mins")))
  (is (= 300000 (time/readable->ms "5 MINUTES")))
  (is (= 300000 (time/readable->ms "5MINUTES")))
  (is (= 5400000 (time/readable->ms "1.5 hours")))
  (is (= 234316800 (time/readable->ms "2.712 days")))
  (is (= 7776000000 (time/readable->ms "3 months")))
  (is (= 31104000000N (time/readable->ms "1y"))))

(deftest readable->ms--customization-test
  (binding [time/*reverse-time-units* {:seconds #{"секунд" "секунди" "секунда"}}]
    (is (= 30000 (time/readable->ms "30 СЕКУНД")))
    (is (= 28000 (time/readable->ms "28 секунд")))
    (is (= 2000 (time/readable->ms "2 секунди")))
    (is (= 21000 (time/readable->ms "21 секунда")))
    (is (= 2800 (time/readable->ms "2.8 секунд")))))

(deftest readable->ms-errors-test
  (is (nil? (time/readable->ms "1")))
  (is (nil? (time/readable->ms "1DAYZ")))
  (is (nil? (time/readable->ms "1.2.3"))))

(deftest readable->ms-ambiguity-test
  (binding [time/*reverse-time-units* {:minutes #{"m"}
                                       :months  #{"m" "months"}}]
    (is (thrown? AssertionError (time/readable->ms "3m")))))