(ns prettier.time
  (:require [clojure.string :as str]
            [prettier.util :as u]))

(def ^:dynamic *time-hierarchy*
  [[:milliseconds 1]
   [:seconds 1000]
   [:minutes 60]
   [:hours 60]
   [:days 24]
   [:months 30]
   [:years 12]])

;; If provided only one time unit use decimal
(def ^:dynamic *time-units*
  {:milliseconds "milliseconds"
   :seconds      "seconds"
   :minutes      "minutes"
   :hours        "hours"
   :days         "days"
   :months       "months"
   :years        "years"})

(def ^:dynamic *reverse-time-units*
  {:milliseconds #{"ms" "msec" "msecs" "millisecond" "milliseconds"}
   :seconds #{"s" "sec" "secs" "second" "seconds"}
   :minutes #{"m" "min" "mins" "minute" "minutes"}
   :hours #{"h" "hs" "hour" "hours"}
   :days #{"d" "ds" "day" "days"}
   :months #{"mon" "month" "months"}
   :years #{"y" "ys" "year" "years"}})

(def ^:dynamic *time-unit-gap* " ")
(def ^:dynamic *time-unit-separator* " ")
(def ^:dynamic *time-decimal-format* "%.1f")

(defn- time-hierarchy-reductions []
  (reductions
    (fn [[_ v1] [u2 v2]]
      [u2 (* v1 v2)]) *time-hierarchy*))

;;;

(defn ms->readable [ms]
  {:pre [(and (not (empty? *time-units*))
              (integer? ms)
              (> ms 0))]}
  (let [effective-time-units
        (->> (time-hierarchy-reductions)
             (filter (fn [[t _]] (*time-units* t)))
             (map (fn [[t _ :as v]] (conj v (*time-units* t))))
             (reverse))]
    ;; If only one time unit requested, show decimals
    (if (= 1 (count effective-time-units))
      (let [[t amount] (first effective-time-units)]
        (-> (format *time-decimal-format* (/ (* 1.0 ms) amount))
            (str *time-unit-gap* (*time-units* t))))
      ;; Calculate [unit amount] chunks
      (loop [[unit & units] effective-time-units num ms chunks []]
        (cond
          unit
          (let [[t amount] unit
                [p1 p2] ((juxt quot rem) num amount)]
            (recur units p2 (conj chunks [t p1])))
          :else (->> chunks
                     (filter (fn [[_ v]] (> v 0)))
                     (map (fn [[t v]] (str v *time-unit-gap* (*time-units* t))))
                     (str/join *time-unit-separator*)))))))

(defn readable->ms [s]
  (let [[num unit] (u/number-and-unit s)
        unit (some-> unit str/lower-case)
        known-time-units (->> (time-hierarchy-reductions)
                              (into {}))]
    (some->> *reverse-time-units*
             (map (fn [[unit-key candidates]]
                    (when (candidates unit) unit-key)))
             (remove nil?)
             ;; ambiguity check
             ((fn [v]
                (if (> (count v) 1)
                  (throw (AssertionError. (str "Ambiguity for " '*reverse-time-units* ":" v)))
                  v)))
             (first)
             (known-time-units)
             (* num)
             (bigint))))