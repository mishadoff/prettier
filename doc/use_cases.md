# Use Cases

## Duplicates

Consider you get a `coll` of items and you need to make sure 
it does not contain duplicates.

Common approach is expressed in following snippet.

```clojure
(when-not (= (count coll) 
             (count (distinct coll)))
  (println "Coll contains duplicates" coll))
```

It may produce log like this

```
[ERROR] Coll contans duplicates [1 2 3 4 5 1]
```

We recommend to use `util/duplicates` as it shows you exactly what duplicates do you have.

```clojure
(let [duplicates (util/duplicates coll)]
 (when-not (empty? duplicates)
   (println "Coll contains duplicates" duplicates)))
```

Now it's much clearer what duplicate do we have

```
[ERROR] Coll contains duplicates #{1}
```