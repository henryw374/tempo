# Tempo

cross-platform Clojure time library

Status - pre-alpha. The underlying Javascript time library is still under development. When it has
stabilized, application developers targeting the browser will need to include their own
script to bring in a polyfill if the end-user's browser does not yet have the platform API required.

## Rationale 

[Tick](https://github.com/juxt/tick) is a full-featured cross-platform date-time library,
and is great for end users who will benefit from that functionality. For Clojurescript users
it comes at a cost of [significant build size](https://github.com/juxt/tick/blob/master/docs/cljs.adoc#optional-timezone--locale-data-for-reducing-build-size).
For many potential users the 'cost' of that build size will be negligible, because their 
builds will easily load fast enough for their end-users. 

On the other hand, Javascript's existing platform Date object has [well documented, "won't fix" issues](https://www.youtube.com/watch?v=aVuor-VAWTI),
and since a Date instance represents the start of a millisecond on the timeline, it is a poor
choice when using it to represent entities such as a calendar date, like 2020-02-02 for example. 
Fortunately,
work is underway to build a [new platform Date-time library for Javascript](https://github.com/tc39/proposal-temporal)
which (perhaps unsurprisingly) has a lot of overlap with Java's java.time library.

For Clojurescript application users who need the smallest possible build size, or for 
libraries wishing to use cross-platform date-time capabilities, Tick's build size and installation 
requirements make it not ideal at present. It
may be possible to implement Tick on the Temporal API, but it would be a huge amount of work to
both 

* fill in the parts of the java.time API missing from Temporal and 
* add enough testing to feel comfortable that the Clojure and Clojurescript APIs had parity.  

The `Tempo` library aims for a small API built using the common parts of java.time and Temporal,
so should suit both Clojure(Script) application builders who need a small cljs build size and library
developers who need to include some date-time capability.  
  
## About

* Zero dependency => platform APIs only
* Use java.time on jvm and Temporal on js runtimes
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





;

```

## TBD 

* observed behaviour will be that of the host api
  * maybe not necessarily exactly the same on both platforms???
  * maybe need to use PeriodDuration on jvm??
    