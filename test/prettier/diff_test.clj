(ns prettier.diff-test
  (:require [clojure.test :refer :all]
            [prettier.diff :as diff]))

(deftest maps--basic-added-deleted-test
  (is (= [(diff/->MapEntryAdded [:a] 1)] (diff/maps {} {:a 1})))
  (is (= [(diff/->MapEntryDeleted [:a] 1)] (diff/maps {:a 1} {})))
  (is (= [(diff/->MapEntryAdded [:a] 1)
          (diff/->MapEntryAdded [:b] 2)
          (diff/->MapEntryAdded [:c] 3)] (diff/maps {} {:a 1 :b 2 :c 3})))
  (is (= [(diff/->MapEntryDeleted [:a] 1)
          (diff/->MapEntryAdded [:b] 2)] (diff/maps {:a 1} {:b 2}))))

(deftest maps--nested-added-deleted-test
  (is (= [(diff/->MapEntryAdded [:a :b :c] 1)] (diff/maps {} {:a {:b {:c 1}}})))
  (is (= [(diff/->MapEntryAdded [:d] 2)
          (diff/->MapEntryAdded [:a :b :c] 1)] (diff/maps {} {:a {:b {:c 1}} :d 2})))
  (is (= [(diff/->MapEntryDeleted [:a :b :d] 1)
          (diff/->MapEntryAdded [:a :b :c] 2)] (diff/maps {:a {:b {:d 1}}} {:a {:b {:c 2}}}))))

(deftest maps--basic-edited-test
  (is (= [(diff/->MapValueEdited [:a] 1 2)] (diff/maps {:a 1} {:a 2})))
  (is (= [(diff/->MapValueEdited [:a :b] 1 2)] (diff/maps {:a {:b 1}} {:a {:b 2}})))
  (is (= [(diff/->MapValueEdited [:c] 1 2)
          (diff/->MapValueEdited [:a :b] 1 2)] (diff/maps {:a {:b 1} :c 1} {:a {:b 2} :c 2}))))

(deftest maps--basic-renamed-test
  (is (= [(diff/->MapKeyRenamed [:a] [:b] 1)] (diff/maps {:a 1} {:b 1})))
  (is (= [(diff/->MapKeyRenamed [:a :b] [:b] 1)] (diff/maps {:a {:b 1}} {:b 1})))
  (is (= [] (diff/maps {:a {:b 1}} {:a {:b 1}}))))