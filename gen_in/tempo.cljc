(ns com.widdindustries.gen.gen-in.tempo)

(ns com.widdindustries.tempo
  ""
  (:refer-clojure :exclude [min max > < >= <= >> <<])
  #?(:cljay
     (:import
       [java.time Clock MonthDay ZoneId ZoneOffset Instant Duration Period DayOfWeek Month ZonedDateTime LocalTime LocalDateTime LocalDate Year YearMonth ZoneId OffsetDateTime OffsetTime]
       [java.time.temporal Temporal TemporalAmount]
       [java.util Date])
     :cljc (:require     
             [cljc.java-time.local-date]
             [cljc.java-time.local-date-time]
             [cljc.java-time.local-time]
             [cljc.java-time.clock]
             [cljc.java-time.instant]
             [cljc.java-time.zone-id]
             [cljc.java-time.zoned-date-time]
             [cljc.java-time.year-month]
             [cljc.java-time.month-day]
             [cljc.java-time.period]
             [cljc.java-time.duration]
             #?@(:cljcs [[java.time :refer [Clock ZoneId ZoneOffset Instant Duration Period DayOfWeek Month ZonedDateTime LocalTime
                                            MonthDay LocalDateTime LocalDate Year YearMonth OffsetDateTime OffsetTime]]
                         [cljs.java-time.extend-eq-and-compare]])
             )
     :cljs (:require [com.widdindustries.tempo.cljs-protocols :as cljs-protocols]
                     [com.widdindustries.tempo.js-temporal-entities :as entities]
                     [com.widdindustries.tempo.js-temporal-methods :as methods]
                     [com.widdindustries.tempo.clock :as clock]))
  #?(:cljcc
     #?(:cljay(:import
               [java.time Clock MonthDay ZoneId ZoneOffset Instant Duration Period DayOfWeek Month ZonedDateTime LocalTime LocalDateTime LocalDate Year YearMonth ZoneId OffsetDateTime OffsetTime]
               [java.time.temporal Temporal TemporalAmount]))
     )
  ;(:require [com.widdindustries.tempo.js-temporal-entities :as entities])
  )

#?(:cljay (set! *warn-on-reflection* true))

(defn extend-all-cljs-protocols []
  #?(:cljs
     (cljs-protocols/extend-all)))

(defn period?           [v] #?(:cljs (instance? entities/duration v)
                               :cljay(instance? Period v)
                               ))
(defn duration?         [v] #?(:cljay(instance? Duration v)
                               :cljs (instance? entities/duration v)))
(defn instant?          [v] #?(:cljay(instance? Instant v)
                               :cljs (instance? entities/instant v)))
(defn date?             [v] #?(:cljay(instance? LocalDate v)
                               :cljs (instance? entities/date v)))
(defn datetime?        [v] #?(:cljay(instance? LocalDateTime v)
                               :cljs (instance? entities/datetime v)))
(defn time?             [v] #?(:cljay(instance? LocalTime v)
                               :cljs (instance? entities/time v)))
(defn monthday?        [v] #?(:cljay(instance? MonthDay v)
                               :cljs (instance? entities/monthday v)))
(defn yearmonth?       [v] #?(:cljay(instance? YearMonth v)
                               :cljs (instance? entities/yearmonth v)))
(defn timezone?          [v] #?(:cljay(instance? ZoneId v)
                               :cljs (instance? entities/timezone v)))
(defn zdt?  [v] #?(:cljay(instance? ZonedDateTime v)
                               :cljs (instance? entities/zdt v)))

;;; manipulating temporal objects

(defn >> [temporal temporal-amount]
  #?(:cljay(.plus  ^Temporal temporal ^TemporalAmount temporal-amount)
     :cljs (.add ^js temporal temporal-amount)))

(defn << [temporal temporal-amount]
  #?(:cljay(.minus  ^Temporal temporal ^TemporalAmount temporal-amount)
     :cljs (.subtract ^js temporal temporal-amount)))

(defn zone-system-default []
  #?(:cljay (ZoneId/systemDefault)
     :cljc (cljc.java-time.zone-id/system-default)
     :cljs (clock/time-zone)))

;; construction of clocks
(defn clock-fixed [instant zone]
  #?(:cljay (Clock/fixed ^Instant instant ^ZoneId zone)
     :cljc (cljc.java-time.clock/fixed instant zone)
     :cljs (clock/fixed-clock instant zone)))

(defn clock-system-default-zone 
  "a ticking clock having the ambient zone"
  []
  #?(:cljay (Clock/systemDefaultZone)
     :cljc (cljc.java-time.clock/system-default-zone)
     :cljs js/Temporal.Now))

(defn greater [x y]
  (if (neg? (compare x y)) y x))

(defn max
  "Find the latest of the given arguments. Callers should ensure that no
  argument is nil."
  [arg & args]
  (assert (every? some? (cons arg args)))
  (reduce #(greater %1 %2) arg args))

(defn lesser [x y]
  (if (neg? (compare x y)) x y))

(defn min
  "Find the earliest of the given arguments. Callers should ensure that no
  argument is nil."
  [arg & args]
  (assert (every? some? (cons arg args)))
  (reduce #(lesser %1 %2) arg args))

(defn <
  ([_x] true)
  ([x y] (neg? (compare x y)) )
  ([x y & more] (if (< x y)
                  (if (next more)
                    (recur y (first more) (next more))
                    (< y (first more)))
                  false)))

(defn <=
  ([_x] true)
  ([x y] (not (pos? (compare x y))))
  ([x y & more] (if (<= x y)
                  (if (next more)
                    (recur y (first more) (next more))
                    (<= y (first more)))
                  false)))

(defn >
  ([_x] true)
  ([x y] (pos? (compare x y)))
  ([x y & more] (if (> x y)
                  (if (next more)
                    (recur y (first more) (next more))
                    (> y (first more)))
                  false)))

(defn >=
  ([_x] true)
  ([x y] (not (neg? (compare x y))))
  ([x y & more] (if (>= x y)
                  (if (next more)
                    (recur y (first more) (next more))
                    (>= y (first more)))
                  false)))


