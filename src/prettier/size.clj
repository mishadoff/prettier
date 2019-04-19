(ns prettier.size)

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
  (let [[_ p1 p2 unit] (re-matches #"(\d+)(\.\d+)?\s*(.*)" s)
        order (->> *size-parseable-units*
                   (map-indexed (fn [idx re]
                                  [(some->> unit
                                            (re-matches re))
                                   idx]))
                   (filter first)
                   first
                   second)]
    (when (and p1 order)
      (-> (str p1 p2)
          (bigdec)
          (* (->> (repeat order 1024)
                  (reduce *')))
          (bigint)))))