(ns com.widdindustries.tempo
  #?(:cljs (:require [com.widdindustries.tempo.cljs-protocols :as cljs-protocols]
                     [com.widdindustries.tempo.js-temporal-entities :as entities]
                     [com.widdindustries.tempo.js-temporal-methods :as methods]
                     [com.widdindustries.tempo.clock :as clock])
     :clj
     (:import
       [java.util Date]
       [java.time Clock MonthDay ZoneId ZoneOffset Instant Duration Period DayOfWeek Month ZonedDateTime LocalTime LocalDateTime LocalDate Year YearMonth ZoneId OffsetDateTime OffsetTime]
       [java.time.temporal Temporal TemporalAmount])))

(defn extend-all-cljs-protocols []
  #?(:cljs
     (cljs-protocols/extend-all)))

(defn period?           [v] #?(:clj (instance? Period v)
                               :cljs (instance? entities/duration v)))
(defn duration?         [v] #?(:clj (instance? Duration v)
                               :cljs (instance? entities/duration v)))
(defn instant?          [v] #?(:clj (instance? Instant v)
                               :cljs (instance? entities/instant v)))
(defn date?             [v] #?(:clj (instance? LocalDate v)
                               :cljs (instance? entities/date v)))
(defn date-time?        [v] #?(:clj (instance? LocalDateTime v)
                               :cljs (instance? entities/datetime v)))
(defn time?             [v] #?(:clj (instance? LocalTime v)
                               :cljs (instance? entities/time v)))
(defn month-day?        [v] #?(:clj (instance? MonthDay v)
                               :cljs (instance? entities/month-day v)))
(defn year-month?       [v] #?(:clj (instance? YearMonth v)
                               :cljs (instance? entities/year-month v)))
(defn zone-id?          [v] #?(:clj (instance? ZoneId v)
                               :cljs (instance? entities/time-zone v)))
(defn zoned-date-time?  [v] #?(:clj (instance? ZonedDateTime v)
                               :cljs (instance? entities/zdt v)))
  ;todo - these entities don't exist in cljs  
;(defn year?             [v] #?(:clj (instance? Year v)
;                               :cljs (instance? js/Temporal. v))
;(defn day-of-week?      [v] #?(:clj (instance? DayOfWeek v)
;                               :cljs (instance? js/Temporal. v))
;(defn month?            [v] #?(:clj (instance? Month v)
;                                 :cljs (instance? js/Temporal. v))


;; construction from clocks
(defn date-now
  ([] #?(:clj (LocalDate/now)
         :cljs (clock/plain-date-iso)))
  ([clock]
   #?(:clj (LocalDate/now ^Clock clock)
      :cljs (clock/plain-date-iso clock))))

;todo - clock construction of other entities

;; construction from strings
(defn date-parse [s]
  #?(:clj (LocalDate/parse s)
     :cljs (methods/from entities/date s)))

(defn instant-parse [s]
  #?(:clj (Instant/parse s)
     :cljs (methods/from entities/instant s)))

(defn duration-parse [s]
  #?(:clj (Duration/parse s)
     :cljs (methods/from entities/duration s))) 

(defn period-parse [s]
  #?(:clj (Period/parse s)
     :cljs (duration-parse s)))

(defn zone-parse [^String s]
  #?(:clj (ZoneId/of s)
     :cljs (methods/from entities/time-zone s)))
;todo - string construction of other entities

;;; manipulating temporal objects

(defn >> [temporal temporal-amount]
  #?(:clj (.plus  ^Temporal temporal ^TemporalAmount temporal-amount)
     :cljs (.add ^js temporal temporal-amount)))

(defn << [temporal temporal-amount]
  #?(:clj (.minus  ^Temporal temporal ^TemporalAmount temporal-amount)
     :cljs (.subtract ^js temporal temporal-amount)))

(defn zone-system-default []
  #?(:clj (ZoneId/systemDefault)
     :cljs (clock/time-zone)))

;;; manipulating temporalamount objects

(defn duration->negated [d]
  #?(:clj (.negated ^Duration d)
     :cljs (.negated ^js d)))

(def duraion-zero (duration-parse "PT0S"))

(defn duration-negative? [d]
  #?(:clj (.isNegative ^Duration d)
     :cljs (neg? (.-sign ^js d))))


;; construction of clocks
(defn clock-fixed [instant zone]
  #?(:clj (Clock/fixed ^Instant instant ^ZoneId (zone-parse zone))
     :cljs (clock/fixed-clock instant zone)))

(defn clock-system-default-zone []
  #?(:clj (Clock/systemDefaultZone)
     :cljs js/Temporal.now))

;entities
;* extract fields - from temporal and temporalamount
; instant->epoch-millis
; zdt->hours
; date->years

;* constructors with fields or maps? ->datetime(date, time)
;->datetime({date, time, hours}) - use biggest bits first

;* truncate

;* with - change field of temporal
; date-with-day
;date-with-year

;* until - diff between 2 temporal
;* +/- for temporalamount
;* max/min
;* bounds-of-type. min date, max possible date?
;* preds
;* >=,<= etc operators

;* clock constructors... system, fixed, offset