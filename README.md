# Tempo

<!-- [![Clojars Project](https://img.shields.io/clojars/v/com.widdindustries/cljc.java-time.svg)](https://clojars.org/com.widdindustries/cljc.java-time)-->
[![Tests build](https://github.com/henryw374/tempo/actions/workflows/tests.yaml/badge.svg)](https://github.com/henryw374/tempo/actions/workflows/tests.yaml)
<!-- [![bb compatible](https://raw.githubusercontent.com/babashka/babashka/master/logo/badge.svg)](https://babashka.org)-->

Zero-dependency Clojure(Script) API to java.time on the JVM and Temporal on JS runtimes

Learn Tempo [live in a browser REPL](https://widdindustries.com/tempo-docs/public/)

## About

### java.time vs Temporal

java.time and Temporal have some overlap with respect to concepts and naming. [See here for a brief introduction and overview](https://widdindustries.com/blog/ecma-temporal-vs-java-time.html)

The below graph shows the entities in Temporal. If you know java.time and you squint a bit, it will look familiar to
you.

![graph of entities in Temporal](https://tc39.es/proposal-temporal/docs/object-model.svg)

Tempo leverages common ground between java.time and Temporal to create an API is that should cover the vast majority of use cases and should leave users rarely needing to drop to the platform APIs. Following is some more detail:

*features of only java.time*

* parsing non-iso
  strings ([Temporal may have this in the future](https://github.com/js-temporal/proposal-temporal-v2/issues/2))
* 2 types to represent temporal-amounts: `Duration` and `Period`
* OffsetDateTime, OffsetTime, Month, Year and DayOfWeek entities
    * Tempo adds a cljs version of DayOfWeek, so there is e.g. `t/weekday-saturday`
    * OffsetDateTime & OffsetTime are not in Tempo
    * Month and Year are just represented by integers in Tempo

*features of only temporal*

* Duration type matching ISO spec
* user-controllable rounding and conflict resolution - Tempo doesn't expose this and chooses same behaviour as java.time
* first-class support for non-ISO calendars

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

[Tick](https://github.com/juxt/tick) is great for application developers who want a
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

As of March 2025

* [Temporal](https://github.com/tc39/proposal-temporal) has semi-stabilized at
  `ecma stage 3`, meaning implementors
  can still suggest changes 
* See [current browser support on MDN](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Temporal)
* Until widely available in browsers, a polyfill (e.g. [this](https://github.com/fullcalendar/temporal-polyfill)) of Temporal can be used.

```html
    <script>
        if(!window.Temporal){
          document.write('<script src="https://cdn.jsdelivr.net/npm/temporal-polyfill@0.3.0-beta.1/global.min.js"><\/script>');         
                  }
    </script>
```


### Require and use

```clojure
(ns my.cljc.namespace
  (:require [com.widdindustries.tempo :as t]
            [time-literals.read-write]))

(t/date-parse "2020-02-02")

; optionally, print objects as data-literals
(time-literals.read-write/print-time-literals-clj!)
(time-literals.read-write/print-time-literals-cljs!)

;optional - make clojure.core fns =,sort,compare etc work for all js/Temporal entities
(t/extend-all-cljs-protocols)

```

Now, learn the API [live in a browser REPL](https://widdindustries.com/tempo-docs/public/)

## Dev

see dev.clj for instructions  

