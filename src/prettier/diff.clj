(ns prettier.diff)

;; Change Types
;; =BASIC======================
;; ADDED   {} -> {:a 1}
;; DELETED {:a 1} -> {}
;; =GUESS======================
;; RENAMED {:a 1} -> {:b 1}  as  DELETED A with V, ADDED B with V
;; CHANGED {:a 2} -> {:a 3}  as  DELETED A with V, ADDED B with V

(defn- map-into-kv-pairs [m]
  (seq m))

(map-into-kv-pairs {:a 1})

;;;

(defn maps [before after]
  ;; process added
  ;; process deleted
  )