# `prettier.diff`

`(:require [prettier.diff :as diff])`

Diff module shows changes between data structures.
Currently, only **maps** supported.

## maps

For maps diff handles basic changes, like 'Added' and 'Deleted' 

```clojure
(diff/maps {:name "John"} {:city "New York"})
=>
(#prettier.diff.MapEntryDeleted{:key [:name], :value "John"}
 #prettier.diff.MapEntryAdded{:key [:city], :value "New York"})
```

Also, it is smart enough to identify value editing.

```clojure
(diff/maps {:name "John"} {:name "Ivan"})
=> (#prettier.diff.MapValueEdited{:key [:name], :value-from "John", :value-to "Ivan"})
```

For most cases it can even handle key renaming.

```clojure
(diff/maps {:name "John"} {:person-name "John"})
=> (#prettier.diff.MapKeyRenamed{:key-from [:name], :key-to [:person-name], :value "John"})
```

All that works for nested maps
and extremely helpful for debugging config changes

```clojure
(diff/maps {:http {:server {:host "mishadoff.com"
                            :port 8080}
                   :api {:endpoint "/api/v1"}}
            :auth {:token "Xz12Hfep1m)f__f"}}
           {:http {:server {:host "mishadoff.com"
                            :port 8092}
                   :api {:endpoint "/api/v3"}}
            :auth {:refresh-token "Xz12Hfep1m)f__f"}})
=>
(#prettier.diff.MapValueEdited{:key [:http :server :port], :value-from 8080, :value-to 8092}
 #prettier.diff.MapValueEdited{:key [:http :api :endpoint], :value-from "/api/v1", :value-to "/api/v3"}
 #prettier.diff.MapKeyRenamed{:key-from [:auth :token], :key-to [:auth :refresh-token], :value "Xz12Hfep1m)f__f"})
```

# `prettier.size`

`(:require [prettier.size :as size])`

Size module can convert bytes to human readable representation.

```clojure
(size/bytes->readable 1000000)
=> "976.6 KB"

(size/bytes->readable (* 5 1024 1024 1024))
=> "5.0 GB"
```

It uses binary prefixes (kibi-, mebi-), 
which are powers of 1024 instead of 1000.

To switch back to classical units with power of 1000 use `*power*`

```clojure
(binding [size/*power* 1000]
  (size/bytes->readable (* 5 1000 1000 1000)))
=> "5.0 GB"
```

Other rendering parameters can be customized as well

```clojure
(binding [size/*size-abbreviations* ["bytes" "kilobytes" "megabytes" "gigabytes"]
          size/*size-decimal-format* "%.0f"
          size/*size-over-limit* "...too much..."
          size/*size-gap* " | "]
  (size/bytes->readable 23000))
=> "22 | kilobytes"   
```

Most libraries accept size values in bytes, 
so it is convenient to provide configuration in readable format
and convert to raw bytes representation, if needed

```clojure
(size/readable->bytes "1kb")
=> 1024
(size/readable->bytes "500mb")
=> 524288000
```

Custom parsing options could be provided using bindings.

```clojure
(binding [size/*size-parseable-units* [#"(?i)byte" #"(?i)kib" #"(?i)mib"]]
  (size/readable->bytes "12Mib"))
=> 12582912N
``` 

# `prettier.time`

`(:require [prettier.time :as time])`

How often do you add something like 

```clojure
(let [time-taken (- (System/currentTimeInMillis) before)]
  (log/info "Time taken" time-taken))
```

to calculate elapsed time? 
And spend non-trivial amount of time 
to understand what 23674133 means. Well, until now.

```clojure
(time/ms->readable 23674133)
=> "6 hours 34 minutes 34 seconds 133 milliseconds"
```

To force elapsed time to be decimal number in one specific unit -
redefine `*time-units*` 

```clojure
(binding [time/*time-units* {:hours "hours"}]
  (time/ms->readable 23674133))
=> "6.6 hours"
```

To customize the output, use bindings.  
Notice, some units can be omitted if not needed.

```clojure
(binding [time/*time-units* {:minutes "m"
                             :hours   "h"
                             :days    "d"}
          time/*time-unit-gap* ""
          time/*time-unit-separator* ""]
  (time/ms->readable 23674133))
=> "6h34m"
```

Alternatively, you can provide time period 
and parse back to milliseconds.

```clojure
(time/readable->ms "1.5 hours")
=> 5400000
``` 

Warning: only one time unit supported so far

