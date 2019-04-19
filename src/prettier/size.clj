(ns prettier.size
  (:require [clojure.spec.alpha :as s]))

(def ^:dynamic *size-abbreviations*
  ["B" "KB" "MB" "GB" "TB" "PB" "EB" "ZB" "YB"])

(def ^:dynamic *size-decimal-format* "%.1f")
(def ^:dynamic *size-gap* " ")
(def ^:dynamic *size-over-limit* "Infinity")

;;;

(defn bytes->readable
  "Transform integer number of bytes into human readable format"
  [b]
  {:pre [(and (integer? b) (>= b 0))]}
  (loop [order 0 bnum (* 1.0 b)]
    (cond
      (>= order (count *size-abbreviations*)) *size-over-limit*
      (<= 0 bnum 1023)
      (let [effective-decimal-fmt (if (zero? order) "%.0f" *size-decimal-format*)]
            (format (str effective-decimal-fmt *size-gap* "%s")
                    bnum
                    (nth *size-abbreviations* order)))
      :else (recur (inc order) (/ bnum 1024)))))

;;;

(defn readable->bytes
  "Transform readable size string into bytes integer"
  [s]
  {:pre [(string? s)]}
  1)