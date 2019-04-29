# `prettier.diff`

TODO

# `prettier.size`

```clojure
(:require [prettier.size :as size])
```

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

```clojure
(:require [prettier.time :as time])
```

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

