(ns prettier.diff)

;; =GUESS======================
;; RENAMED {:a 1} -> {:b 1}  as  DELETED A with V, ADDED B with V
;; CHANGED {:a 2} -> {:a 3}  as  DELETED A with V, ADDED A with V2

(defn- map-into-kv-pairs
  "Transforms nested map into key value pairs. Return vec of pairs
   Each pair represented as vec [key-path value]"
  [m]
  (loop [[k & ks] (->> m keys (map vector)) pairs []]
    (let [v (get-in m k)]
      (cond (nil? k) pairs
            (map? v) (recur (->> (keys v)
                                 (map (partial conj k))
                                 (concat ks))
                            pairs)
            :else (recur ks (conj pairs [k v]))))))

;;;

(defn maps
  "Find a difference between two maps.
   Returns a sequence of changes, which if applied in order
   transform map 'before' into map 'after'
   Maps could be nested.

   Examples of changes:
    {:change :deleted :path [:a :b]    :value 10}
    {:change :added   :path [:c :d :e] :value 1}
   "
  [before after]
  (let [b-pairs (->> before map-into-kv-pairs (into {}))
        a-pairs (->> after map-into-kv-pairs (into {}))
        changes-by-type (fn [a b type]
                          (->> a
                               (map (fn [[k v]]
                                      (when (not= v (get b k))
                                        {:change type :path k :value v})))
                               (remove nil?)))]
    (concat
      (changes-by-type b-pairs a-pairs :deleted)
      (changes-by-type a-pairs b-pairs :added))))