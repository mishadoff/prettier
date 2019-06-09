(ns prettier.util-test
  (:require [clojure.test :refer :all]
            [prettier.util :as util]))

(deftest distance--empty-test
  (is (= 0 (util/distance nil [])))
  (is (= 0 (util/distance [] nil)))
  (is (= 0 (util/distance [] [])))
  (is (= 0 (util/distance nil nil)))
  (is (= 0 (util/distance "" nil)))
  (is (= 0 (util/distance "" [])))
  (is (= 0 (util/distance "" ""))))

(deftest distance--basic-test
  (is (= 1 (util/distance "beer" "bear")))
  (is (= 1 (util/distance "rain" "pain")))
  (is (= 7 (util/distance "advance" "")))
  (is (= 3 (util/distance "elephant" "relevant")))
  (is (= 5 (util/distance "beer" "vodka")))
  (is (= 3 (util/distance [:a :b :c] [:c :d :e])))
  (is (= 1 (util/distance [:a :b :c] [:a :d :c])))
  (is (= 1 (util/distance [:a :b :c :d] [:a :b :d])))
  (is (= 1 (util/distance ["NAME" "SURNAME" "AGE"] ["NAME" "AGE"])))
  (is (= 1 (util/distance {:a 1 :b 2 :c 3} {:a 2 :b 2 :c 3}))))