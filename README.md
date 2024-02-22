# Tempo

<!-- [![Clojars Project](https://img.shields.io/clojars/v/com.widdindustries/cljc.java-time.svg)](https://clojars.org/com.widdindustries/cljc.java-time)-->
[![Tests build](https://github.com/henryw374/tempo/actions/workflows/tests.yaml/badge.svg)](https://github.com/henryw374/tempo/actions/workflows/tests.yaml)
<!-- [![bb compatible](https://raw.githubusercontent.com/babashka/babashka/master/logo/badge.svg)](https://babashka.org)-->


zero-dependency, cross-platform Clojure time library

*Status* - pre-alpha. 

* The underlying [Javascript platform time API](https://github.com/tc39/proposal-temporal) has semi-stabilized at ecma `stage 3` - meaning implementors
can still suggest changes. When it has reached `stage 4`, application developers targeting the browser will need to include their own
script to bring in a polyfill if the end-user's browser does not yet have the platform API required.
* Not that much of the `Tempo` API exists at the moment - see `Usage` below for all ideas & plans. 
FYI The API is planned to mostly consist of generated code.
* Feedback welcome!

## About

* Zero dependency => platform APIs only - see [comparison of java.time vs temporal](https://widdindustries.com/ecma-temporal-vs-java-time/)
* platform+performance friendly - max DCE-ability for cljs, reflection-free on jvm. Likely means little/no polymorphism.
* explicitly named conversions from type to type. no keyword arguments in functions
* no implicit/contextual use of ambient zone or clock
* API based on mnemonics
* small feature set - aim for 80% of everyday date/time use cases.
* totally ignore non-ISO8601 calendars. 
* data-literals - same ones as [time-literals](https://github.com/henryw374/time-literals) (IOW just a new artifact from that project)

### java.time vs Temporal

java.time and Temporal share some concepts, but both have some unshared concepts. Naming is also partially shared. [See here for a brief introduction and overview](https://widdindustries.com/blog/ecma-temporal-vs-java-time.html)

The below graph shows the entities in Temporal. If you know java.time and you squint a bit, it will look familiar to you. 

![graph of entities in Temporal](https://tc39.es/proposal-temporal/docs/object-model.svg)

Tempo tries to find obvious common ground between java.time and Temporal and put it in an API. Following is some more detail:

*just java.time*

* parsing non-iso strings
* 2 types to represent temporal amounts Duration and Period
* clojure identity and comparison fns: =,>,>= etc all 'just work' - so these are added to Temporal objects in Tempo
* fixed & offset clocks - so these are added in cljs Tempo
* OffsetDateTime, OffsetTime, Month, Year and DayOfWeek entities 
  * Tempo adds DayOfWeek to cljs, so there is e.g. `t/weekday-saturday`
  * OffsetDateTime & OffsetTime are not in Tempo
  * Month and Year are just represented by integers in Tempo

*just temporal*

* Duration type matching ISO spec
* user-controllable rounding and conflict resolution - Tempo doesnt expose this and chooses same behaviour as java.time
* first-class support for non-ISO calendars

*both*

* formatting non-iso strings - this is not in Tempo (yet)

## Rationale 

[Tick](https://github.com/juxt/tick) (which I help maintain) is great for application developers who want a 
cross-platform date-time library based on the java.time API. Tick provides much useful functionality
on top of java.time, but users know they can always drop to [cljc.java-time](https://github.com/henryw374/cljc.java-time),
to access the full java.time API directly when needed.
 
For application developers targeting Clojurescript in the browser,
Tick comes with a cost of [additional build size](https://github.com/juxt/tick/blob/master/docs/cljs.adoc#optional-timezone--locale-data-for-reducing-build-size).
For many use-cases, the 'cost' of that build size will be negligible, because the 
build size will not impact significantly on user's experience of the application: ie neither load time
or memory usage will be an issue given anticipated network and device capabilities - see [this experiment](https://widdindustries.com/blog/clojurescript-datetime-lib-comparison.html) proving that. 

At the other end of the date-time spectrum, Javascript's existing platform Date object has [well documented, "won't fix" issues](https://www.youtube.com/watch?v=aVuor-VAWTI),
which you see as either outright bugs or just a bizarre API.
Also, since there is only one platform `Date` entity, an instance of which represents the start of a millisecond on the global timeline, there 
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
artifact.

## Usage

### Depend 

There is no tempo maven artifact atm.

Depend on tempo via deps.edn:

```clojure
{:deps {com.widdindustries/tempo {:git/url "https://github.com/henryw374/tempo.git"
                                  :sha "abc"
                                  }}}
```

### Setup

```clojure
(ns my.cljc.namespace
 (:require [com.widdindustries.tempo :as t]))

;optional - make clojure.core fns =,sort,compare etc work for all js/Temporal entities
(t/extend-all-cljs-protocols)
; or just extend them to entities you are using
(t/extend-cljs-protocols-to-instant)
```

### Construction and access/get

#### Clocks

```clojure
;fixed in time and place
(t/clock-fixed (t/instant-now) "Europe/Paris")

;  ambient place, ticking clock
(t/clock-system-default-zone)

; offset existing clock by specified millis
(t/clock-offset clock -5)

```

#### Time zones 

```clojure
(t/timezone-parse "Europe/London")
```

Where a timezone is accessed from an object, or passed into an object, only the string representation is used, referred to as `timezone_id` 

```clojure
(t/zdt->timezone_id zdt)
(t/zdt-from {:datetime datetime :timezone_id timezone})
```

#### Temporals

```clojure

; Tempo construction and access is based on mnemonics

; the first word in the function is the entity name of the subject of the operation

; for example, if I want to construct a date or access its parts the function will start t/date-,
; similarly for a zone-date-time, it will be t/zdt-*
(t/date-now)
(t/date-now clock)
(t/date-parse "2020-02-02") ;iso strings only
(t/date-from {:year 2020 :month 2 :day 2})
; the -from functions accept a map of components which is sufficient to build the entity
(t/datetime-from {:date (t/date-parse "2020-02-02") :time (t/time-now)})
; or equivalently
(t/datetime-from {:year 2020 :month 2 :day 2 :time (t/time-now)})
; with -from, you can use smaller or larger components. 
; larger ones take precedence. below, the :year is ignored, because the :date took precedence (being larger) 
(t/datetime-from {:year 2021 :date (t/date-parse "2020-02-02") :time (t/time-now)})

; to get parts of an entity, start with the subject and add ->
(t/date->yearmonth (t/date-now))
(t/date->month (t/date-now))
(t/zdt->nanos (t/zdt-now))
(-> (t/instant-now) (t/instant->epochmillis))

```

#### Temporal-amounts

A temporal-amount is a quantity of time, e.g. hour, month, second.

Temporal-Amount entities are represented differently in java.time and Temporal, but with some overlap.

An `alpha` ns (groan!) exists which contains a few functions for working with temporal-amounts.

If not sufficient, use reader conditionals in your code to construct/manipulate as appropriate.

```clojure

(require '[com.widdindustries.tempo.duration-alpha :as d])

(d/duration-parse "PT0.001S")

### Manipulation 

aka construction a new entity from one of the same type

#### Temporal-amounts

```clojure

(require '[com.widdindustries.tempo.duration-alpha :as d])
(t/+ (d/duration-parse "PT3H") (d/duration-parse "PT3S"))

```

#### Temporals

```clojure

(require '[com.widdindustries.tempo.duration-alpha :as d])

;; move date forward 3 days
(t/>> (t/date-now) (d/period-parse "P3D"))

(-> (t/date-now) (t/with 3030 t/years-property))

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

see dev.clj for instructions  

