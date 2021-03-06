(ns prettier.size
  (:require [prettier.util :as u]))

(def ^:dynamic *size-abbreviations*
  ["B" "KB" "MB" "GB" "TB" "PB" "EB" "ZB" "YB"])

(def ^:dynamic *power* 1024)

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
      (<= 0 bnum (dec *power*))
      (let [effective-decimal-fmt (if (zero? order) "%.0f" *size-decimal-format*)]
            (format (str effective-decimal-fmt *size-gap* "%s")
                    bnum
                    (nth *size-abbreviations* order)))
      :else (recur (inc order) (/ bnum *power*)))))

;;;

(def ^:dynamic *size-parseable-units*
  [#"(?i)b"
   #"(?i)k|kb"
   #"(?i)m|mb"
   #"(?i)g|gb"
   #"(?i)t|tb"
   #"(?i)p|pb"
   #"(?i)e|eb"
   #"(?i)z|zb"
   #"(?i)y|yb"])

(defn readable->bytes
  "Transform readable size string into bytes integer"
  [s]
  {:pre [(string? s)]}
  (let [[num unit] (u/number-and-unit s)
        order (->> *size-parseable-units*
                   (map-indexed (fn [idx re]
                                  [(some->> unit
                                            (re-matches re))
                                   idx]))
                   (filter first)
                   first
                   second)]
    (when (and num (>= num 0) order)
      (-> num
          (* (->> (repeat order *power*)
                  (reduce *')))
          (bigint)))))