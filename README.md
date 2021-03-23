# Tempo

zero-dependency, cross-platform Clojure time library

*Status* - pre-alpha. 

* The underlying [Javascript platform time API](https://github.com/tc39/proposal-temporal) has semi-stabilized at ecma `stage 3` - meaning implementors
can still suggest changes. When it has reached `stage 4`, application developers targeting the browser will need to include their own
script to bring in a polyfill if the end-user's browser does not yet have the platform API required.
* Not that much of the `Tempo` API exists at the moment - see `Usage` below for all ideas & plans. FYI The API is planned to mostly consist generated code.
* Feedback welcome!

## About

* Zero dependency => platform APIs only - see [comparison of java.time vs temporal](https://widdindustries.com/ecma-temporal-vs-java-time/)
* platform+performance friendly - max DCE-ability for cljs, reflection-free on jvm. Likely means little/no polymorphism.
* API based on mnemonics => lots of generated functions. 
* small feature set - aim for 80% of everyday date/time use cases.
* totally ignore non-ISO8601 calendars. 
* data-literals - same ones as [time-literals](https://github.com/henryw374/time-literals) (iow just a new version of that library)

## Rationale 

[Tick](https://github.com/juxt/tick) (which I help maintain) is great for application developers who want a 
cross-platform date-time library based on the java.time API. Tick provides much useful functionality
on top of java.time, but users know they can always drop to [cljc.java-time](https://github.com/henryw374/cljc.java-time),
to access the full java.time API directly when needed.
 
For application developers targeting Clojurescript in the browser,
Tick comes with a cost of [additional build size](https://github.com/juxt/tick/blob/master/docs/cljs.adoc#optional-timezone--locale-data-for-reducing-build-size).
For many use-cases, the 'cost' of that build size will be negligible, because the 
build size will not impact significantly on user's experience of the application: ie neither load time
or memory usage will be an issue given anticipated network and device capabilities. 

At the other end of the date-time spectrum, Javascript's existing platform Date object has [well documented, "won't fix" issues](https://www.youtube.com/watch?v=aVuor-VAWTI),
which you see as either outright bugs or just a bizarre API.
Also, since there is only one platform Date entity, an instance of which represents the start of a millisecond on the global timeline, there 
are many use cases it does not serve well, such as representing a calendar date, like 2020-02-02 for example. 

Fortunately,
work is underway to build a [new platform Date-time library for Javascript](https://github.com/tc39/proposal-temporal)
which (perhaps unsurprisingly) has a lot of overlap with Java's java.time library.

For Clojure(script) application users who need the smallest possible build size, or for 
library authors wishing to use cross-platform date-time capabilities, Tick's build size and installation 
requirements make it not ideal at present. 

It may be possible to implement Tick on the js/Temporal API, but it would be a huge amount of work to
both: 

* fill in all the parts of the java.time API missing from Temporal (bc Tick at present is sugar on top of the java.time API on both the JVM *and* Javascript) 
* add enough testing to feel comfortable that the Clojure and Clojurescript APIs had full parity.  

`Tempo` aims for a low-surprise/low-sugar API built using just the common parts of `java.time` and `Temporal`,
so should suit both Clojure(Script) application builders who need a small cljs build size and library
developers who need to include some date-time capability. 

This means for example that custom formatting and parsing are not in this library, since there is no common
functionality for that between java.time and js/Temporal. (But might be [in the future](https://github.com/js-temporal/proposal-temporal-v2/issues/2))

It is expected that in future, a version of Tick could be built on top of `tempo` - probably as a separate Tick-lite
dependency.

### Having said all that... 

tl;dr this is something of a thought experiment at the moment for what I would look for in a 
core Clojure(Script) time library. A zero-dependency, cross-platform API was my ideal when I first
started on the 'cross-platform time-API' path in early 2017 - so this API feels like some kind of 
end-state. 

## Usage

;todo - show graph of entities
; distinguish temporal-amounts and temporals
;todo - explain no OffsetDateTime, Month, Year, DayOfWeek 

### Setup

```clojure
(ns my.cljc.namespace
 (:require [com.widdindustries.tempo :as t]))

;optional - make clojure.core fns =,sort,compare etc work for js/Temporal objects
(t/extend-all-cljs-protocols)
; or just extend it to entities you are using
(t/extend-cljs-protocols-to-instant)
```

### Construction and access/get

#### Clocks

;system, fixed, offset

#### Temporals

```clojure
;tempo construction and access is based on mnemonics

; the first word in the function is the entity name of the subject of the operation

; for example, if I want to construct a date or access its parts the function will start t/date-,
; similarly for a zone-date-time, it will be t/zdt-*
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

```

#### Temporal-amounts
```clojure

(t/period->days (t/period-parse "P3Y5M3D")) ; > 3

(t/duration->as-minutes (t/duration-parse "PT3H")) ; > 180

; following won't exist bc what length year? month?
;(t/period->as-days (t/period-parse "P3Y5M3D"))

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

; todo - is this easily doable with platform api??
(-> (t/date-now) (t/truncate-to-month))
(-> (t/instant-now) (t/truncate-to-month))

```

### Comparison

#### Temporal
```clojure

;only entities of the same type can be compared

(t/>= a b)


(t/max a b c)

; you must specify unit
(t/until a b :minutes)

```

### Predicates

```clojure

(t/date? x)
```

## Dev 

* start figwheel from compilation.clj and visit http://localhost:9503 to run tests 
  
## Work in progress todo list

add temporal as submodule and use to dev/test via node/CI
   git@github.com:tc39/proposal-temporal.git
   ("node --experimental-modules --no-warnings --icu-data-dir node_modules/full-icu -r ./lib/init.js")

api todo - see todos in tempo.cljc

## TBD 

* observed behaviour will be that of the host api
  * maybe not necessarily exactly the same on both platforms? but afaik defaults for ambiguity resolution
  are the same on both atm
 
    