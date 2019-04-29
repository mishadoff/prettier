(ns prettier.diff-test
  (:require [clojure.test :refer :all]
            [prettier.diff :as diff]))

(deftest maps--basic-added-deleted--test
  (is (= [{:change :added :path [:a] :value 1}] (diff/maps {} {:a 1})))
  (is (= [{:change :deleted :path [:a] :value 1}] (diff/maps {:a 1} {})))
  (is (= [{:change :added :path [:a] :value 1}
          {:change :added :path [:b] :value 2}
          {:change :added :path [:c] :value 3}] (diff/maps {} {:a 1 :b 2 :c 3})))
  (is (= [{:change :deleted :path [:a] :value 1}
          {:change :added :path [:a] :value 2}] (diff/maps {:a 1} {:a 2}))))

(deftest maps--nested-added-deleted--test
  (is (= [{:change :added :path [:a :b :c] :value 1}] (diff/maps {} {:a {:b {:c 1}}})))
  (is (= [{:change :added :path [:d] :value 2}
          {:change :added :path [:a :b :c] :value 1}] (diff/maps {} {:a {:b {:c 1}} :d 2})))
  (is (= [{:change :deleted :path [:a :b :d] :value 1}
          {:change :added :path [:a :b :c] :value 1}] (diff/maps {:a {:b {:d 1}}} {:a {:b {:c 1}}}))))