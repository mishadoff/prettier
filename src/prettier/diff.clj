(ns prettier.diff)

;; Registered changes
(defrecord MapEntryAdded [key value])
(defrecord MapEntryDeleted [key value])
(defrecord MapValueEdited [key value-from value-to])
(defrecord MapKeyRenamed [key-from key-to value])

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

(defn- maps-basic-changes
  "Detect basic changes between unnested maps
   Handles MapEntryAdded and MapEntryDelete types."
  [before after change-fn]
  (->> before
       (map (fn [[k v]]
              (when (not= v (get after k))
                (change-fn k v))))
       (remove nil?)))

;;;

(defn maps-edited-changes
  "Detects 'edited' changes using basic 'added' and 'deleted' changes
   Uses squash rule to merge two changes into one
    MapEntryDeleted{:key [:a], :value 1}
    MapEntryAdded{:key [:a], :value 2}
                vvv
    MapValueChanged{:key [:a] :value-from 1 :value-to 2}

   Returns vector of 2 values
    - edited changes to be added to result
    - added and deleted changes used for merge to be deleted from result"
  [basic-changes]
  (->> basic-changes
       (group-by :key)
       (filter (fn [[_ changes]]
                 (and (= (count changes) 2)
                      (= #{MapEntryAdded MapEntryDeleted}
                         (->> changes (map type) (into #{}))))))
       (map (fn [[k changes]]
              (let [deleted (->> changes (filter #(= MapEntryDeleted (type %))) first)
                    added (->> changes (filter #(= MapEntryAdded (type %))) first)]
                [(->MapValueEdited k (:value deleted) (:value added)) changes])))
       (reduce (fn [[acc-e acc-changes] [e changes]]
                 [(conj acc-e e) (concat changes acc-changes)])
               [[] []])))

;;;

(defn maps-renamed-changes
  "Detects 'renamed' changes using basic :added and :deleted changes
   Uses squash rule to merge two changes into one
    MapEntryDeleted{:key [:a], :value 1}
      MapEntryAdded{:key [:b], :value 1}
                vvv
    MapKeyRenamed{:key-from [:a], :key-to [:b], :value 1}

   Returns vector of 2 values
    - renamed changes to be added to result
    - added and deleted changes used for merge to be deleted from result"
  [basic-changes]
  (->> basic-changes
       (group-by :value)
       (filter (fn [[_ changes]]
                 (and (= (count changes) 2)
                      (= #{MapEntryAdded MapEntryDeleted}
                         (->> changes (map type) (into #{}))))))
       (map (fn [[v changes]]
              (let [deleted (->> changes (filter #(= MapEntryDeleted (type %))) first)
                    added (->> changes (filter #(= MapEntryAdded (type %))) first)]
                [(->MapKeyRenamed (:key deleted) (:key added) v) changes])))
       (reduce (fn [[acc-e acc-changes] [e changes]]
                 [(conj acc-e e) (concat changes acc-changes)])
               [[] []])))

;;;

(defn maps
  "Find a difference between two maps.
   Returns a sequence of changes, which if applied in order
   transform map 'before' into map 'after'
   Maps could be nested.

   Examples of basic changes:
    MapEntryDeleted{:key [:a], :value 1}
    MapEntryAdded{:key [:a :b :c], :value 10}

   Can squash basic changes to detect more advanced changes:
    MapValueEdited{:key [:a], :value-from 1, :value-to 2}
    MapValueRenamed{:key-from [:a], :key-to [:b], :value 1}
   "
  [before after]
  (let [b-pairs (->> before map-into-kv-pairs (into {}))
        a-pairs (->> after map-into-kv-pairs (into {}))
        deleted (maps-basic-changes b-pairs a-pairs ->MapEntryDeleted)
        added (maps-basic-changes a-pairs b-pairs ->MapEntryAdded)
        added-and-deleted (concat deleted added)
        enrich (fn [changes changes-fn]
                 (let [[to-add to-remove] (changes-fn changes)
                       to-remove-set (into #{} to-remove)]
                   (concat (remove to-remove-set changes) to-add)))]
    (-> added-and-deleted
        (enrich maps-edited-changes)
        (enrich maps-renamed-changes))))