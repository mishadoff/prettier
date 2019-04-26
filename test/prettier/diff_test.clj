(ns prettier.diff-test
  (:require [clojure.test :refer :all]
            [prettier.diff :as diff]))

(deftest diff-maps--test
  (is (= [{:change :added :path [:a] :value 1}] (diff/maps {} {:a 1})))
  (is (= [{:change :deleted :path [:a] :value 1}] (diff/maps {:a 1} {})))


  )