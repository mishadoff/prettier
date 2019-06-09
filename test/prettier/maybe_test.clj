(ns prettier.maybe-test
  (:require [clojure.test :refer :all]
            [prettier.maybe :as maybe]))

(deftest maybe--basic-test
  (is (= ["string"] (maybe/candidates ["string" "number" "type"] "string")))
  (is (= ["string"] (maybe/candidates ["string" "number" "type"] "sting")))
  (is (= ["string"] (maybe/candidates ["string" "number" "type"] "sing")))
  (is (= ["string"] (maybe/candidates ["string" "number" "type"] "strang")))
  (is (= ["number"] (maybe/candidates ["string" "number" "type"] "numberic")))
  (is (= [] (maybe/candidates ["string" "number" "type"] "numeric")))
  (is (= [] (maybe/candidates ["string" "number" "type"] "integer"))))

(deftest maybe--custom-distance-test
  (binding [maybe/*distance* 3]
    (is (= ["number"] (maybe/candidates ["string" "number" "type"] "numeric")))
    (is (= ["type"] (maybe/candidates ["string" "number" "type"] "taype")))))