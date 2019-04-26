(ns prettier.util
  (:require [clojure.string :as str]))

(defn number-and-unit
  "Parse a string and returns number and unit.
   Both number and unit is required, nil otherwise"
  [s]
  (let [[_ p1 p2 unit] (re-matches #"(\-?\d+)(\.\d+)?\s*(.*)" s)]
    (if-not (or (str/blank? p1) (str/blank? unit))
      [(-> (str p1 p2) (bigdec))
       unit])))
