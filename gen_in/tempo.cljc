(ns com.widdindustries.gen.gen-in.tempo)

(ns com.widdindustries.tempo
  ""
  (:refer-clojure :exclude [min max > < >= <= >> <<])
  #?(:cljay
     (:import
       [java.time Clock MonthDay ZoneId ZoneOffset Instant Duration Period DayOfWeek Month ZonedDateTime LocalTime LocalDateTime LocalDate Year YearMonth ZoneId OffsetDateTime OffsetTime]
       [java.time.temporal Temporal TemporalAmount TemporalUnit ChronoUnit ChronoField]
       [java.util Date])
     :cljs (:require [com.widdindustries.tempo.cljs-protocols :as cljs-protocols]
             [com.widdindustries.tempo.js-temporal-entities :as entities]
             [com.widdindustries.tempo.js-temporal-methods :as methods]
             [com.widdindustries.tempo.clock :as clock]))
  ;(:require [com.widdindustries.tempo.js-temporal-entities :as entities])
  )

#?(:cljay (set! *warn-on-reflection* true))

(defn extend-all-cljs-protocols []
  #?(:cljs
     (cljs-protocols/extend-all)))

(defn period? [v] #?(:cljs (instance? entities/duration v)
                     :cljay (instance? Period v)
                     ))
(defn duration? [v] #?(:cljay (instance? Duration v)
                       :cljs (instance? entities/duration v)))
(defn instant? [v] #?(:cljay (instance? Instant v)
                      :cljs (instance? entities/instant v)))
(defn date? [v] #?(:cljay (instance? LocalDate v)
                   :cljs (instance? entities/date v)))
(defn datetime? [v] #?(:cljay (instance? LocalDateTime v)
                       :cljs (instance? entities/datetime v)))
(defn time? [v] #?(:cljay (instance? LocalTime v)
                   :cljs (instance? entities/time v)))
(defn monthday? [v] #?(:cljay (instance? MonthDay v)
                       :cljs (instance? entities/monthday v)))
(defn yearmonth? [v] #?(:cljay (instance? YearMonth v)
                        :cljs (instance? entities/yearmonth v)))
(defn timezone? [v] #?(:cljay (instance? ZoneId v)
                       :cljs (instance? entities/timezone v)))
(defn zdt? [v] #?(:cljay (instance? ZonedDateTime v)
                  :cljs (instance? entities/zdt v)))

;;; manipulating temporal objects

(defn timezone-system-default []
  #?(:cljay (ZoneId/systemDefault)
     :cljs (clock/timezone)))

;; construction of clocks
(defn clock-fixed [instant ^String zone-str]
  #?(:cljay (Clock/fixed ^Instant instant (ZoneId/of zone-str))
     :cljs (clock/fixed-clock instant zone-str)))

(defn clock-system-default-zone
  "a ticking clock having the ambient zone. "
  []
  #?(:cljay (Clock/systemDefaultZone)
     :cljs js/Temporal.Now))

(defn clock-offset-millis [clock offset-millis]
  #?(:cljay (Clock/offset clock (Duration/ofMillis offset-millis))
     :cljs (clock/offset-clock-millis clock offset-millis)))

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
  ([x y] (neg? (compare x y)))
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

(defprotocol Unit
  (unit-field [_])
  (unit-amount [_])
  (unit-accessor [_ x]))

(defprotocol Property 
  (unit [_])
  (field [_]))

(def nanos-property #?(:cljay (reify Property (unit [_] ChronoUnit/NANOS) (field [_] ChronoField/NANO_OF_SECOND))       :cljs (reify Property (field [_] "nanosecond")(unit [_] (reify Unit (unit-amount [_] "nanoseconds") (unit-field [_] "nanosecond") (unit-accessor [_ ^js x] (.-nanos x)))))))
(def micros-property #?(:cljay (reify Property (unit [_] ChronoUnit/MICROS) (field [_] ChronoField/MICRO_OF_SECOND))    :cljs (reify Property (field [_] "microsecond")(unit [_] (reify Unit (unit-amount [_] "microseconds") (unit-field [_] "microsecond") (unit-accessor [_ ^js x] (.-micros x)))))))
(def millis-property #?(:cljay (reify Property (unit [_] ChronoUnit/MILLIS) (field [_] ChronoField/MILLI_OF_SECOND))    :cljs (reify Property (field [_] "millisecond")(unit [_] (reify Unit (unit-amount [_] "milliseconds") (unit-field [_] "millisecond") (unit-accessor [_ ^js x] (.-millis x)))))))
(def seconds-property #?(:cljay (reify Property (unit [_] ChronoUnit/SECONDS) (field [_] ChronoField/SECOND_OF_MINUTE)) :cljs (reify Property (field [_] "second")(unit [_] (reify Unit (unit-amount [_] "seconds") (unit-field [_] "second") (unit-accessor [_ ^js x] (.-seconds x)))))))
(def minutes-property #?(:cljay (reify Property (unit [_] ChronoUnit/MINUTES) (field [_] ChronoField/MINUTE_OF_HOUR))   :cljs (reify Property (field [_] "minute")(unit [_] (reify Unit (unit-amount [_] "minutes") (unit-field [_] "minute") (unit-accessor [_ ^js x] (.-minutes x)))))))
(def hours-property #?(:cljay (reify Property (unit [_] ChronoUnit/HOURS) (field [_] ChronoField/HOUR_OF_DAY))          :cljs (reify Property (field [_] "hour")(unit [_] (reify Unit (unit-amount [_] "hours") (unit-field [_] "hour") (unit-accessor [_ ^js x] (.-hours x)))))))
(def days-property #?(:cljay (reify Property (unit [_] ChronoUnit/DAYS) (field [_] ChronoField/DAY_OF_MONTH))           :cljs (reify Property (field [_] "day")(unit [_] (reify Unit (unit-amount [_] "days") (unit-field [_] "day") (unit-accessor [_ ^js x] (.-days x)))))))
(def months-property #?(:cljay (reify Property (unit [_] ChronoUnit/MONTHS) (field [_] ChronoField/MONTH_OF_YEAR))      :cljs (reify Property (field [_] "month")(unit [_] (reify Unit (unit-amount [_] "months") (unit-field [_] "month") (unit-accessor [_ ^js x] (.-months x)))))))
(def years-property #?(:cljay (reify Property (unit [_] ChronoUnit/YEARS) (field [_] ChronoField/YEAR))                 :cljs (reify Property (field [_] "year")(unit [_] (reify Unit (unit-amount [_] "years") (unit-field [_] "year") (unit-accessor [_ ^js x] (.-years x)))))))

(defn with [temporal value property]
  #?(:cljay (.with ^Temporal temporal ^TemporalField (field property) ^long value)
     :cljs (.with ^js temporal (js-obj (field property) value))))

#_(defn truncate [temporal property]
  #_?(:cljay 
     :cljs ))

(defn until [v1 v2 property]
  #?(:cljay (.until ^Temporal v1 v2 (unit property))
     ;https://tc39.es/proposal-temporal/docs/instant.html#until
     :cljs (-> (.until ^js v1 ^js v2 #js{:smallestUnit (unit-field (unit property)) :largestUnit (unit-field (unit property))})
                (unit-accessor (unit property)))))

(defn >>
  ([temporal temporal-amount]
   #?(:cljay (.plus ^Temporal temporal ^TemporalAmount temporal-amount)
      :cljs (.add ^js temporal temporal-amount)))
  ([temporal amount temporal-property]
   #?(:cljay (.plus ^Temporal temporal amount ^TemporalUnit (unit temporal-property))
      :cljs (.add ^js temporal (js-obj (unit-amount (unit temporal-property)) amount)))))

(defn <<
  ([temporal temporal-amount]
   #?(:cljay (.minus ^Temporal temporal ^TemporalAmount temporal-amount)
      :cljs (.subtract ^js temporal temporal-amount)))
  ([temporal amount temporal-property]
   #?(:cljay (.minus ^Temporal temporal amount ^TemporalUnit (unit temporal-property))
      :cljs (.subtract ^js temporal (js-obj (unit-amount (unit temporal-property)) amount)))))

(defprotocol WeekDay 
  (weekday-number [_])
  (english-name [_]))

#?(:cljay 
   (extend-protocol WeekDay
     DayOfWeek 
     (weekday-number [x] (.getValue x))
     (english-name [x] (str x))))

(def weekday-monday  #?(:cljay DayOfWeek/MONDAY :cljs (reify WeekDay (weekday-number [_] 1) (english-name [_] "MONDAY"))))
(def weekday-tuesday #?(:cljay DayOfWeek/TUESDAY :cljs (reify WeekDay (weekday-number [_] 2) (english-name [_] "TUESDAY"))))
(def weekday-wednesday #?(:cljay DayOfWeek/WEDNESDAY :cljs (reify WeekDay (weekday-number [_] 3) (english-name [_] "WEDNESDAY"))))
(def weekday-thursday #?(:cljay DayOfWeek/THURSDAY :cljs (reify WeekDay (weekday-number [_] 4) (english-name [_] "THURSDAY"))))
(def weekday-friday #?(:cljay DayOfWeek/FRIDAY :cljs (reify WeekDay (weekday-number [_] 5) (english-name [_] "FRIDAY"))))
(def weekday-saturday #?(:cljay DayOfWeek/SATURDAY :cljs (reify WeekDay (weekday-number [_] 6) (english-name [_] "SATURDAY"))))
(def weekday-sunday #?(:cljay DayOfWeek/SUNDAY :cljs (reify WeekDay (weekday-number [_] 7) (english-name [_] "SUNDAY"))))

(def weekday-number->weekday 
  {1 weekday-monday
  2 weekday-tuesday
  3 weekday-wednesday
  4 weekday-thursday
  5 weekday-friday
  6 weekday-saturday
  7 weekday-sunday})