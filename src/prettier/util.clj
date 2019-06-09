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

(defn distance
  "Calculates levenshtein distance between two sequences
  Levenshtein distance roughly defines number of changes
  which required to apply to transform 'seq1' into 'seq2'
  Example: consider strings as sequence of letters
  (distance \"bear\" \"beer\") => 1 (1st change from 'a' to 'e')
  (distance \"dog\" \"doll\") => 2 (1st change from 'g' to 'l', 2nd change insertion of 'l')"
  [seq1 seq2]
  (let [seq1v (into [] seq1)
        get (fn [v i] (get v i ::not-found))
        init-row (into [] (range (inc (count seq1))))
        advance-row (fn [row e]
                      (reduce (fn [acc i]
                                (cond (= 0 i) (conj acc (inc (get row i)))
                                      (= e (get seq1v (dec i))) (conj acc (get row (dec i)))
                                      :else (conj acc
                                                  (inc (min (get row (dec i))
                                                            (get row i)
                                                            (peek acc))))))
                              []
                              init-row))]
    (peek (reduce advance-row init-row seq2))))
