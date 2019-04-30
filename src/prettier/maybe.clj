(ns prettier.maybe
  (:require [prettier.util :as u]))

(def ^:dynamic *distance* 2)

(defn candidates
  "Returns candidates from the list which are similar to the original value.
   Ordered by distance ascending, which mean more close candidates come first.

   Examples: (cadidates [\"string\" \"integer\" \"double\"] \"sting\") => [\"string\"]
  "
  [values v]
  (->> values
       (map (juxt (partial u/distance v) identity))
       (filter (comp (partial >= *distance*) first))
       (sort-by first)
       (map second)))