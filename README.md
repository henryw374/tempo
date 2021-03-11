### Work in progress todo list

add temporal as submodule and use to dev/test via node
   git@github.com:tc39/proposal-temporal.git
   ("node --experimental-modules --no-warnings --icu-data-dir node_modules/full-icu -r ./lib/init.js")

api todo :

* extract fields - from temporal and temporalamount
* with - change field of temporal
* until - diff between 2 temporal
* +/- for temporalamount
* max/min
* bounds-of-type. min date, max possible date?
* truncate
* preds
* >=,<= etc operators
* constructors with fields or maps?

# Tempo

cross-platform Clojure time library

Status - pre-alpha. The underlying Javascript time library is still under development. When it has
stabilized, application developers targeting the browser will need to include their own
script to bring in a polyfill if the end-user's browser does not yet have the platform API required.

## Rationale 

[Tick](https://github.com/juxt/tick) is great for application developers who need a 
full-featured cross-platform date-time library. For application developers targeting
Clojurescript in the browser,
it comes at a cost of [significant build size](https://github.com/juxt/tick/blob/master/docs/cljs.adoc#optional-timezone--locale-data-for-reducing-build-size).
For many use-cases, the 'cost' of that build size will be negligible, because the 
build size will not impact significantly on user's experience of the application: neither load time
of memory usage will be an issue given anticipated network and device capabilities. 

At the other end of the date-time spectrum, Javascript's existing platform Date object has [well documented, "won't fix" issues](https://www.youtube.com/watch?v=aVuor-VAWTI).
Also, since there is only one Date entity, an instance of which represents the start of a millisecond on the timeline, there 
are many use cases it does not serve well, such as representing a calendar date, like 2020-02-02 for example. 

Fortunately,
work is underway to build a [new platform Date-time library for Javascript](https://github.com/tc39/proposal-temporal)
which (perhaps unsurprisingly) has a lot of overlap with Java's java.time library.

For Clojurescript application users who need the smallest possible build size, or for 
library authors wishing to use cross-platform date-time capabilities, Tick's build size and installation 
requirements make it not ideal at present. It
may be possible to implement Tick on the js/Temporal API, but it would be a huge amount of work to
both: 

* fill in the parts of the java.time API missing from Temporal (bc Tick at present is sugar on top of the java.time API on both the JVM *and* Javascript) 
* add enough testing to feel comfortable that the Clojure and Clojurescript APIs had parity.  

`Tempo` aims for a small API built using just the common parts of java.time and Temporal,
so should suit both Clojure(Script) application builders who need a small cljs build size and library
developers who need to include some date-time capability. 

This means for example that custom formatting and parsing are not in this library, since there is no common
functionality for that between java.time and js/Temporal.   
  
## About

* Zero dependency => platform APIs only
* Use java.time on jvm and Temporal on js runtime
* fully DCE friendly
* small feature set - aim for 80% of everyday use cases.

## Usage 

```clojure
;cljs only - make =,sort etc work for Temporal objects
(require '[com.widdindustries.tempo.cljs-protocols :as cljs-protocols])
(cljs-protocols/extend-all)

;; cross-platform
(require '[com.widdindustries.tempo :as t])

(def a-date (t/now-date))

;; move date forward 3 days
(t/>> a-date (t/parse-period "P3D"))


```

## TBD 

* observed behaviour will be that of the host api
  * maybe not necessarily exactly the same on both platforms???
  * maybe need to use PeriodDuration on jvm??
    