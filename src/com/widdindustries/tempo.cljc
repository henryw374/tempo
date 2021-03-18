(ns com.widdindustries.tempo
  #?(:cljs (:require [com.widdindustries.tempo.cljs-protocols :as cljs-protocols]
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
                               :cljs (instance? js/Temporal.Duration v)))
(defn duration?         [v] #?(:clj (instance? Duration v)
                               :cljs (instance? js/Temporal.Duration v)))
(defn instant?          [v] #?(:clj (instance? Instant v)
                               :cljs (instance? js/Temporal.Instant v)))
(defn date?             [v] #?(:clj (instance? LocalDate v)
                               :cljs (instance? js/Temporal.PlainDate v)))
(defn date-time?        [v] #?(:clj (instance? LocalDateTime v)
                               :cljs (instance? js/Temporal.PlainDateTime v)))
(defn time?             [v] #?(:clj (instance? LocalTime v)
                               :cljs (instance? js/Temporal.PlainTime v)))
(defn month-day?        [v] #?(:clj (instance? MonthDay v)
                               :cljs (instance? js/Temporal.MonthDay v)))
(defn year-month?       [v] #?(:clj (instance? YearMonth v)
                               :cljs (instance? js/Temporal.YearMonth v)))
(defn zone-id?          [v] #?(:clj (instance? ZoneId v)
                               :cljs (instance? js/Temporal.TimeZone v)))
(defn zoned-date-time?  [v] #?(:clj (instance? ZonedDateTime v)
                               :cljs (instance? js/Temporal.ZonedDateTime v)))
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
         :cljs (js/Temporal.now.plainDateISO)))
  ([clock]
   #?(:clj (LocalDate/now ^Clock clock)
      :cljs (.plainDateISO clock))))

;todo - clock construction of other entities

;; construction from strings
(defn date-parse [s]
  #?(:clj (LocalDate/parse s)
     :cljs (js/Temporal.PlainDate.from s)))

(defn instant-parse [s]
  #?(:clj (Instant/parse s)
     :cljs (js/Temporal.Instant.from s)))

(defn duration-parse [s]
  #?(:clj (Duration/parse s)
     :cljs (js/Temporal.Duration.from s))) 

(defn period-parse [s]
  #?(:clj (Period/parse s)
     :cljs (duration-parse s)))

(defn zone-parse [^String zone-id]
  #?(:clj (ZoneId/of zone-id)
     :cljs (js/Temporal.TimeZone.from zone-id)))
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
     :cljs (js/Temporal.now.timeZone)))

;;; manipulating temporalamount objects

(defn duration-negated [d]
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
