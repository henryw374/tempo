# Tempo

cross-platform Clojure time library

Status - pre-alpha. The underlying Javascript time library is still under development. When it has
stabilized, application developers targeting the browser will need to include their own
script to bring in a polyfill if the end-user's browser does not yet have the platform API required.

tl;dr this is something of a thought experiment at the moment for what I would look for in a 
core Clojure(Script) time library

## Rationale 

[Tick](https://github.com/juxt/tick) (which I help maintain) is great for application developers who want a 
cross-platform date-time library based on the java.time API. Tick provides much useful functionality
on top of java.time, but users know they can always drop to [cljc.java-time](https://github.com/henryw374/cljc.java-time),
to access the java.time API directly when needed.
 
For application developers targeting Clojurescript in the browser,
Tick comes with a cost of an [increased build size](https://github.com/juxt/tick/blob/master/docs/cljs.adoc#optional-timezone--locale-data-for-reducing-build-size).
For many use-cases, the 'cost' of that build size will be negligible, because the 
build size will not impact significantly on user's experience of the application: neither load time
or memory usage will be an issue given anticipated network and device capabilities. 

At the other end of the date-time spectrum, Javascript's existing platform Date object has [well documented, "won't fix" issues](https://www.youtube.com/watch?v=aVuor-VAWTI).
Also, since there is only one Date entity, an instance of which represents the start of a millisecond on the timeline, there 
are many use cases it does not serve well, such as representing a calendar date, like 2020-02-02 for example. 

Fortunately,
work is underway to build a [new platform Date-time library for Javascript](https://github.com/tc39/proposal-temporal)
which (perhaps unsurprisingly) has a lot of overlap with Java's java.time library.

For Clojure(script) application users who need the smallest possible build size, or for 
library authors wishing to use cross-platform date-time capabilities, Tick's build size and installation 
requirements make it not ideal at present. It
may be possible to implement Tick on the js/Temporal API, but it would be a huge amount of work to
both: 

* fill in the parts of the java.time API missing from Temporal (bc Tick at present is sugar on top of the java.time API on both the JVM *and* Javascript) 
* add enough testing to feel comfortable that the Clojure and Clojurescript APIs had parity.  

`Tempo` aims for a low-surprise/low-sugar API built using just the common parts of java.time and Temporal,
so should suit both Clojure(Script) application builders who need a small cljs build size and library
developers who need to include some date-time capability. 

This means for example that custom formatting and parsing are not in this library, since there is no common
functionality for that between java.time and js/Temporal.

### Dev 

* start figwheel from compilation.clj and visit http://localhost:9503 to run tests 
  
### Work in progress todo list

add temporal as submodule and use to dev/test via node
   git@github.com:tc39/proposal-temporal.git
   ("node --experimental-modules --no-warnings --icu-data-dir node_modules/full-icu -r ./lib/init.js")

api todo - see todos in tempo.cljc

## About

* Zero dependency => platform APIs only
* Use java.time on jvm and Temporal on js runtime
* platform friendly - full DCE for cljs, reflection free on jvm
* small feature set - aim for 80% of everyday use cases.

## Usage 

### Setup

get from clojars etc

```clojure
(ns my.cljc.namespace
 (:require [com.widdindustries.tempo :as t]))

;optional - make clojure.core fns =,sort,compare etc work for js/Temporal objects
(t/extend-all-cljs-protocols)
```

### Construction and access

#### Clocks

;system, fixed, offset

#### Temporal-amounts
```clojure

;todo - what does this return?
(t/period->days (t/period-parse "P3Y5M3D"))

```

#### Temporals

```clojure
;tempo construction and access is based on mnemonics

; the first word in the function is the entity name of the subject of the operation

; for example, if I want to construct a date or access its parts the function will start t/date-,
; similarly for a zone-date-time, it will be t/zoned-date-time-*
(t/date-now)
(t/date-parse "2020-02-02") ;iso strings only
(t/date-from {:year 2020 :month 2 :day 2})
; the -from functions accept a map of components which is sufficient to build the entity
(t/datetime-from {:date (t/date-parse "2020-02-02") :time (t/time-now)})
; or equivalently
(t/datetime-from {:year 2020 :month 2 :day 2 :time (t/time-now)})
; with -from, you can use smaller or larger components. 
; larger ones take precedence. below, the :year is ignored, because the :date took precedence (being larger) 
(t/datetime-from {:year 2021 :date (t/date-parse "2020-02-02") :time (t/time-now)})

; to get parts of an entity, start with the subject as before and add ->
(t/date->yearmonth (t/date-now))
(t/date->month (t/date-now))
(t/zdt->nanos (t/zdt-now))
(-> (t/instant-now) (t/instant->epochmillis))

; part-getting could be polymorphic - as it is in tick. could add as higher layer late. at cost of DCE

```

### Manipulation

#### Temporal-amounts

```clojure

(t/+ )

```

#### Temporals

```clojure

;; move date forward 3 days
(t/>> (t/date-now) (t/period-parse "P3D"))

(-> (t/date-now) (t/with {:year 2021 :month 7}))
(-> (t/date-now) (t/with-year 3030))

; todo - is this possible??
(-> (t/date-now) (t/truncate-to-month))
(-> (t/instant-now) (t/truncate-to-month))

```

### Comparison

#### Temporal
```clojure

;only entities of the same type can be compared

(t/>= a b)


(t/max a b c)

(t/until a b)

```

### Predicates

```clojure

(t/date? x)
```


## TBD 

* observed behaviour will be that of the host api
  * maybe not necessarily exactly the same on both platforms???
  * maybe need to use PeriodDuration on jvm??
    