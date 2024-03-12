# Tempo

<!-- [![Clojars Project](https://img.shields.io/clojars/v/com.widdindustries/cljc.java-time.svg)](https://clojars.org/com.widdindustries/cljc.java-time)-->
[![Tests build](https://github.com/henryw374/tempo/actions/workflows/tests.yaml/badge.svg)](https://github.com/henryw374/tempo/actions/workflows/tests.yaml)
<!-- [![bb compatible](https://raw.githubusercontent.com/babashka/babashka/master/logo/badge.svg)](https://babashka.org)-->


Zero-dependency, cross-platform Clojure time library

## About

### java.time vs Temporal

java.time and Temporal have some overlap with respect to concepts and naming. [See here for a brief introduction and overview](https://widdindustries.com/blog/ecma-temporal-vs-java-time.html)

The below graph shows the entities in Temporal. If you know java.time and you squint a bit, it will look familiar to
you.

![graph of entities in Temporal](https://tc39.es/proposal-temporal/docs/object-model.svg)

Tempo tries to find obvious common ground between java.time and Temporal. Following is some more detail:

*just java.time*

* parsing non-iso
  strings ([Temporal may have this in the future](https://github.com/js-temporal/proposal-temporal-v2/issues/2))
* 2 types to represent temporal-amounts: `Duration` and `Period`
* clojure `=`, `compare` and `hash` work - so these are added to Temporal objects in Tempo
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

Since it was introduced in Java 8, use of the java.time API has become more and more widespread because:

* it improves on the legacy `java.util.Date` API
* it is a platform API - developers and library authors can be confident that other developers will know the API and be
  happy to use it.

The same benefits will apply to the Temporal API when it is widely available in browsers.

Cross-platform date/time APIs for Clojure have already proven popular. It seems logical that one should exist targeting
both java.time and Temporal.

However, as stated above, although there is not a 1-1 correspondance between java.time and Temporal, there is sufficient
overlap for a cross platform API that covers the majority of everyday use-cases.

### Why not 'fill in the gaps' to make Temporal like java.time?

There are some obvious benefits to be had if this were done.

However, aside from being a lot of work to do this, Temporal is a different API from java.time. The Temporal authors
have designed it from scratch very deliberately and in so doing have made some different choices from java.time.

Where Temporal and java.time overlap, there is obvious scope for a common API. Where they differ, application developers
can decide on a case by case basis how to tackle that.

### What about Existing Cross-platform date/time APIs?

[Tick](https://github.com/juxt/tick) (which I help maintain) is great for application developers who want a
cross-platform date-time library based on the java.time API. Tick provides much useful functionality
on top of java.time, but users know they can always drop
to [cljc.java-time](https://github.com/henryw374/cljc.java-time),
to access the full java.time API directly when needed.

Even when Temporal is widely available, I would imagine many Clojure developers will want to keep using Tick because

* It is based on the same java.time API in both JVM and Javascript environments - so the full capability of java.time is
  available as required.
* The additional build size of Tick in
  Javascript [does not degrade application performance](https://widdindustries.com/blog/clojurescript-datetime-lib-comparison.html)
* Switching away from it will require significant time investment

Since `tick` is based on `java.time`, in its entirety it is incompatible with Temporal. Having said that a `tempo.tick`
namespace exists which contains a subset of the functions from `tick.core` which are compatible. This is WIP.

## Usage

### Depend

[![Clojars Project](https://img.shields.io/clojars/v/com.widdindustries/tempo.svg)](https://clojars.org/com.widdindustries/tempo)

        ; to get data-literals for java.time and Temporal, also add...


[![Clojars Project](https://img.shields.io/clojars/v/com.widdindustries/time-literals-tempo.svg)](https://clojars.org/com.widdindustries/time-literals-tempo)

* As of March 2024, a polyfill (e.g. [this](https://github.com/fullcalendar/temporal-polyfill)) of Temporal will be required for all javascript environments.
* The new underlying Javascript platform time API [Temporal](https://github.com/tc39/proposal-temporal) has semi-stabilized at
  `ecma stage 3`, meaning implementors
  can still suggest changes - although at this point any changes will be superficial.

### Setup

```clojure
(ns my.cljc.namespace
  (:require [com.widdindustries.tempo :as t]
            [time-literals.read-write]))

; optionally, print objects as data-literals
(time-literals.read-write/print-time-literals-clj!)
(time-literals.read-write/print-time-literals-cljs!)

;optional - make clojure.core fns =,sort,compare etc work for all js/Temporal entities
(t/extend-all-cljs-protocols)

```

### Naming

The naming of entities (ie the in the  graph further up) should be self-explanatory. The java.time `Local` prefix and the Temporal `Plain` prefix have been removed, so e.g. PlainDate/LocalDate is just date.

ZonedDateTime is called `zdt` to keep it short. js/Date and java.util.Date are called `legacydate`


### Clocks

A Clock is required to be able to get the current time/date/timezone etc

```clojure

;  ticking clock in ambient place
(t/clock-system-default-zone)

; ticking clock in specified place
(t/clock-with-zone "Pacific/Honolulu")

; clock fixed in time and place
(t/clock-fixed (t/instant-parse "2020-02-02T00:00:00Z") "Europe/Paris")

; offset existing clock by specified millis
(t/clock-offset clock -5)
```

a clock is then passed as arg to all `now` functions, for example:

```clojure
(t/date-now clock)
```

### Time zones & Offsets

```clojure
(t/timezone-parse "Europe/London")

(t/timezone-now clock)
```

Where a timezone is accessed from an object, or passed into an object, only the string representation can be used, referred
to as `timezone_id`. Call `str` on a timezone to get its id.

```clojure
(t/zdt->timezone_id zdt)
(t/zdt-from {:datetime datetime :timezone_id timezone_id})
```

### Temporal Construction & Access

```clojure

; naming of construction and access functions is based on mnemonics

; the first word in the function is the entity name of the subject of the operation

(t/date-now clock)
(t/date-parse "2020-02-02") ;iso strings only
(t/zdt-now clock)
(t/zdt-parse "2020-02-02...") ;iso strings only

; build from parts
(t/date-from {:year 2020 :month 2 :day 2})
; the -from functions accept a map of components which is sufficient to build the entity
(t/datetime-from {:date (t/date-parse "2020-02-02") :time (t/time-now clock)})
; or equivalently
(t/datetime-from {:year 2020 :month 2 :day 2 :time (t/time-now clock)})
; with -from, you can use smaller or larger components. 
; larger ones take precedence. below, the :year is ignored, because the :date took precedence (being larger) 
(t/datetime-from {:year 2021 :date (t/date-parse "2020-02-02") :time (t/time-now clock)})

; 'add' a field to an object to create a different type
(t/yearmonth+day a-yearmonth 1) ; => a date
(t/yearmonth+day-at-end-of-month a-yearmonth) ; => a date
(t/datetime+timezone a-datetime "Pacific/Honolulu") ; => a zdt

; to get parts of an entity, start with the subject and add ->
(t/date->yearmonth a-date)
(t/date->month a-date)
(t/zdt->nanosecond a-zdt)
(t/instant->epochmillisecond an-instant)

```

#### Properties

Vars such as `t/hours-property` exist in Tempo. These combine the concept of `units` and `fields`, so for example

```clojure
(t/until x y t/days-property) ; how much time in unit days
(t/with x 11 t/days-property) ; set the day of month field to 11
```

Combining the concept of unit and field is a simplification. In some cases it may be an over-simplification, for example `t/days-property` corresponds to the `day of month` field, so if `day of year` was required a new property would have to be created in user space. 

However, as per the stated aim of Tempo to just cover everyday use cases, hopefully the property concept has sufficient benefit to outweigh the cost. 

#### Manipulation

aka construction a new temporal from one of the same type

```clojure

;; move date forward 3 days
(t/>> a-date 3 t/days-property)

;; set a particular field
(t/with a-yearmonth 3030 t/years-property)

; set fields smaller than days (ie hours, mins etc) to zero
(t/truncate x t/days-property)

```

#### Guardrails

Consider the following:

```clojure
(let [start (t/date-parse "2020-01-31")]
  (-> start 
      (t/>> 1 t/months-property)
      (t/<< 1 t/months-property)))
```

If you shift a date forward by an amount, then back by the same amount then one might think that the output would be equal to the
input. In some cases that would happen, but not in the case shown above.

Here's a similar example:

```clojure
(let [start (t/date-parse "2020-02-29")]
  (-> start
      (t/with 2021 t/years-property)
      (t/with 2020 t/years-property)))
```

We increment the year, then decrement it, but the output is not the same as the input.

Both java.time and Temporal work this way and in my experience it is a source of bugs. For this reason, shifting `>>/<<`
and `with` do not work in Tempo if the property is years or months and the subject is not a year-month.

As a safer alternative, I suggest getting the year-month from a temporal first, doing whatever with/shift operations you
like then setting the remaining fields.

If you do not wish to have this guardrail, set `t/*block-non-commutative-operations*` to false

### Comparison

```clojure

;only entities of the same type can be compared

(t/>= a b)

(t/max a b c)

; you must specify property
(t/until a b t/minutes-property)

```

#### Predicates

```clojure

(t/date? x)
```

#### Temporal-amounts

A temporal-amount is an entity representing a quantity of time, e.g. 3 hours and 5 seconds.

Temporal-Amount entities are represented differently in java.time vs Temporal, but with some overlap.

An `alpha` ns (groan!) exists which contains a few functions for working with temporal-amounts.

If not sufficient, use reader conditionals in your code to construct/manipulate as appropriate.

```clojure

(require '[com.widdindustries.tempo.duration-alpha :as d])

(d/duration-parse "PT0.001S")

```

## Dev

see dev.clj for instructions  

