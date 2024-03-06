(ns com.widdindustries.gen.gen-in.tempo)

(ns com.widdindustries.tempo
  ""
  (:refer-clojure :exclude [min max > < >= <= >> <<])
  #?(:cljay
     (:import
       [java.time Clock MonthDay ZoneId ZoneOffset Instant Duration Period DayOfWeek Month ZonedDateTime LocalTime LocalDateTime LocalDate Year YearMonth ZoneId OffsetDateTime OffsetTime]
       [java.time.temporal Temporal TemporalAmount TemporalUnit TemporalAccessor ChronoUnit ChronoField ValueRange]
       [java.util Date])
     :cljs (:require [com.widdindustries.tempo.cljs-protocols :as cljs-protocols]
             [com.widdindustries.tempo.js-temporal-entities :as entities]
             [com.widdindustries.tempo.js-temporal-methods :as methods]
             [com.widdindustries.tempo.clock :as clock]
             [goog.object]))
  ;(:require [com.widdindustries.tempo.js-temporal-entities :as entities])
  )

#?(:cljay (set! *warn-on-reflection* true))

(defn extend-all-cljs-protocols []
  #?(:cljs
     (cljs-protocols/extend-all)))

(defn legacydate? [v]
  #?(:cljs (instance? js/Date v)
     :cljay (instance? java.util.Date v)
     ))
;(defn clock? [v] #?(:cljs nil :cljay (instance? Clock v)))
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

;; construction of clocks
(defn clock-fixed [instant ^String zone-str]
  #?(:cljay (Clock/fixed ^Instant instant (ZoneId/of zone-str))
     :cljs (clock/clock (constantly instant) zone-str)))

(defn clock-with-zone [^String timezone_id]
  #?(:cljay (Clock/system (ZoneId/of timezone_id))
     :cljs (clock/clock js/Temporal.Now.instant timezone_id)))

(defn clock-system-default-zone
  "a ticking clock having the ambient zone. "
  []
  #?(:cljay (Clock/systemDefaultZone)
     :cljs js/Temporal.Now))

(defn clock-offset-millis [clock offset-millis]
  #?(:cljay (Clock/offset clock (Duration/ofMillis offset-millis))
     :cljs (clock/clock
             (fn [] (.add (.instant ^js clock) (js-obj "milliseconds" offset-millis)))
             (clock/timezone_id clock))))

(defn timezone-now
  ([clock] #?(:cljay (.getZone ^Clock clock)
              :cljs (clock/timezone_id clock))))

(defn legacydate->instant [d]
  #?(:cljay (.toInstant ^java.util.Date d)
     :cljs (.toTemporalInstant ^js d)))

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

(defn coincident? [start end event]
  (and (<= start event)
    (>= end event)))

#?(:cljay (defprotocol Property
            (unit [_])
            (field [_])))

#?(:cljay
   (defprotocol HasTime
     (getFractional [_])
     (setFractional [_ _])))

#?(:cljay (defn -millisecond [f]
            (-> (getFractional f) (Duration/ofNanos) (.toMillisPart))))
#?(:cljay (defn -microsecond [f]
            (-> (getFractional f) (/ 1000) long (mod 1000))))
#?(:cljay (defn -nanosecond [f]
            (-> (getFractional f) (mod 1000))))

#?(:cljay
   (extend-protocol HasTime
     ZonedDateTime
     (getFractional [x] (.getNano (.toLocalTime x)))
     (setFractional [x t] (.withNano x t))
     LocalDateTime
     (getFractional [x] (.getNano (.toLocalTime x)))
     (setFractional [x t] (.withNano x t))
     LocalTime
     (getFractional [x] (.getNano x))
     (setFractional [x t] (.withNano x t))
     Instant
     (getFractional [x] (.getNano x))
     (setFractional [x ^long t] (.with x ChronoField/NANO_OF_SECOND t))
     ))

#?(:cljay (def ^ValueRange sub-second-range (ValueRange/of 0 999)))

(def nanoseconds-property
  #?(:cljay (reify
              Object (toString [_] "nanoseconds-property")
              Property
              (unit [_] ChronoUnit/NANOS)
              (field [_] (reify java.time.temporal.TemporalField
                           (rangeRefinedBy [_ _temporal] sub-second-range)
                           (getFrom [_ temporal] (-nanosecond temporal))
                           (adjustInto [_ temporal value]
                             (.checkValidValue sub-second-range value nil)
                             (let [fractional (getFractional temporal)
                                   millis+micros (-> fractional (/ 1000) long (* 1000))
                                   new-fractional (+ millis+micros value)]
                               (-> temporal (setFractional new-fractional)))))))
     :cljs "nanosecond"))


(def microseconds-property #?(:cljay (reify
                                       Object (toString [_] "microseconds-property")
                                       Property (unit [_] ChronoUnit/MICROS)
                                       (field [_] (reify java.time.temporal.TemporalField
                                                    (rangeRefinedBy [_ _temporal] sub-second-range)
                                                    (getFrom [_ temporal] (-microsecond temporal))
                                                    (adjustInto [_ temporal value]
                                                      (.checkValidValue sub-second-range value nil)
                                                      (let [fractional (getFractional temporal)
                                                            millis (-> fractional (/ 1000000) long (* 1000000))
                                                            nanos (-> fractional (mod 1000))
                                                            new-fractional (+ millis nanos (-> value (* 1000)))]
                                                        (-> temporal (setFractional new-fractional)))))))
                              :cljs "microsecond"))

#_(defmethod print-method (type microseconds-property)
    [_, ^java.io.Writer w]
    (print-simple "microseconds-property" w))

(def milliseconds-property #?(:cljay (reify
                                       Object (toString [_] "milliseconds-property")
                                       Property (unit [_] ChronoUnit/MILLIS)
                                       (field [_] (reify java.time.temporal.TemporalField
                                                    (rangeRefinedBy [_ _temporal] sub-second-range)
                                                    (getFrom [_ temporal] (-millisecond temporal))
                                                    (adjustInto [_ temporal value]
                                                      (.checkValidValue sub-second-range value nil)
                                                      (let [fractional (getFractional temporal)
                                                            micros+nanos (-> fractional (mod 1000000))]
                                                        (-> temporal (setFractional (+ micros+nanos (-> value (* 1000000))))))))))
                              :cljs "millisecond"))

#_(defmethod print-method (type milliseconds-property)
    [_, ^java.io.Writer w]
    (print-simple "milliseconds-property" w))

(def seconds-property #?(:cljay (reify
                                  Object (toString [_] "seconds-property")
                                  Property (unit [_] ChronoUnit/SECONDS)
                                  (field [_] ChronoField/SECOND_OF_MINUTE))
                         :cljs "second"))

#_(defmethod print-method (type seconds-property)
    [_, ^java.io.Writer w]
    (print-simple "seconds-property" w))

(def minutes-property #?(:cljay (reify
                                  Object (toString [_] "minutes-property")
                                  Property (unit [_] ChronoUnit/MINUTES)
                                  (field [_] ChronoField/MINUTE_OF_HOUR))
                         :cljs "minute"))

#_(defmethod print-method (type minutes-property)
    [_, ^java.io.Writer w]
    (print-simple "minutes-property" w))

(def hours-property #?(:cljay (reify
                                Object (toString [_] "hours-property")
                                Property (unit [_] ChronoUnit/HOURS)
                                (field [_] ChronoField/HOUR_OF_DAY))
                       :cljs "hour"))

#_(defmethod print-method (type hours-property)
    [_, ^java.io.Writer w]
    (print-simple "hours-property" w))

(def days-property #?(:cljay (reify
                               Object (toString [_] "days-property")
                               Property (unit [_] ChronoUnit/DAYS)
                               (field [_] ChronoField/DAY_OF_MONTH))
                      :cljs "day"))

#_(defmethod print-method (type days-property)
    [_, ^java.io.Writer w]
    (print-simple "days-property" w))

(def months-property #?(:cljay (reify
                                 Object (toString [_] "months-property")
                                 Property (unit [_] ChronoUnit/MONTHS)
                                 (field [_] ChronoField/MONTH_OF_YEAR))
                        :cljs "month"))

#_(defmethod print-method (type months-property)
    [_, ^java.io.Writer w]
    (print-simple "months-property" w))

(def years-property #?(:cljay (reify
                                Object (toString [_] "years-property")
                                Property (unit [_] ChronoUnit/YEARS)
                                (field [_] ChronoField/YEAR))
                       :cljs "year"))

#_(defmethod print-method (type years-property)
    [_, ^java.io.Writer w]
    (print-simple "years-property" w))

(def ^:dynamic *block-non-commutative-operations* true)

(defn throw-if-set-months-or-years [temporal temporal-property]
  (when
    (and *block-non-commutative-operations* 
      (contains? #{years-property months-property} temporal-property)
         (not (or (monthday? temporal) (yearmonth? temporal))))
    (throw (ex-info
             "shifting by years or months yields odd results depending on input. intead shift a year-month, then set non-yearmonth parts"
             {}))))

(defn with [temporal value property]
  (throw-if-set-months-or-years temporal property)
  #?(:cljay (.with ^Temporal temporal ^TemporalField (field property) ^long value)
     :cljs (.with ^js temporal (js-obj property value) (js-obj "overflow" "reject"))))

(defn until [v1 v2 property]
  #?(:cljay (.until ^Temporal v1 v2 (unit property))
     ;https://tc39.es/proposal-temporal/docs/instant.html#until
     :cljs 
     (-> (.until ^js v1 ^js v2 (js-obj "smallestUnit" property
                                 "largestUnit" property))
         (goog.object/get (str property "s")))))

(defn >>
  #_([temporal temporal-property]
     (throw-if-set-months-or-years temporal temporal-amount)
     #?(:cljay (.plus ^Temporal temporal ^TemporalAmount temporal-amount)
        :cljs (.add ^js temporal temporal-amount)))
  ([temporal amount temporal-property]
   (throw-if-set-months-or-years temporal temporal-property)
   #?(:cljay (.plus ^Temporal temporal amount ^TemporalUnit (unit temporal-property))
      :cljs (.add ^js temporal (js-obj (str temporal-property "s") amount)))))

(defn <<
  #_([temporal temporal-amount]
     (throw-if-set-months-or-years temporal temporal-amount)
     #?(:cljay (.minus ^Temporal temporal ^TemporalAmount temporal-amount)
        :cljs (.subtract ^js temporal temporal-amount)))
  ([temporal amount temporal-property]
   (throw-if-set-months-or-years temporal temporal-property)
   #?(:cljay (.minus ^Temporal temporal amount ^TemporalUnit (unit temporal-property))
      :cljs (.subtract ^js temporal (js-obj (str temporal-property "s") amount)))))

(def weekday-monday "MONDAY")
(def weekday-tuesday "TUESDAY")
(def weekday-wednesday "WEDNESDAY")
(def weekday-thursday "THURSDAY")
(def weekday-friday "FRIDAY")
(def weekday-saturday "SATURDAY")
(def weekday-sunday "SUNDAY")

(def weekday-number->weekday
  {1 weekday-monday
   2 weekday-tuesday
   3 weekday-wednesday
   4 weekday-thursday
   5 weekday-friday
   6 weekday-saturday
   7 weekday-sunday})

(def weekday->weekday-number 
  {weekday-monday    1 
   weekday-tuesday   2 
   weekday-wednesday 3 
   weekday-thursday  4 
   weekday-friday    5 
   weekday-saturday  6 
   weekday-sunday    7   })

#?(:clj (defprotocol JavaTruncateable
          (-truncate [_ unit])))

#?(:cljay
   (extend-protocol JavaTruncateable
     ZonedDateTime (-truncate [zdt unit] (.truncatedTo zdt unit))
     LocalDateTime (-truncate [zdt unit] (.truncatedTo zdt unit))
     LocalTime (-truncate [zdt unit] (.truncatedTo zdt unit))
     Instant (-truncate [zdt unit] (.truncatedTo zdt unit))))

(defn truncate [temporal property]
  #?(:cljay (-truncate temporal (unit property))
     :cljs (.round ^js temporal 
             (js-obj "smallestUnit" property "roundingMode" "trunc"))))

(defn get-field [temporal property]
  #?(:cljay (.get ^TemporalAccessor temporal (field property))
     :cljs (goog.object/get temporal property)))

(defn yearmonth+day-at-end-of-month [ym]
  #?(:cljay (.atEndOfMonth ^YearMonth ym)
     :cljs (.toPlainDate ^js ym (js-obj "day" (.-daysInMonth ^js ym)))))

(defn monthday+year [monthday year]
  #?(:cljay (.atYear ^MonthDay monthday ^int year)
     :cljs (.toPlainDate ^js monthday (js-obj "year" year))))

(defn yearmonth+day [yearmonth day]
  #?(:cljay (.atDay ^YearMonth yearmonth ^int day)
     :cljs (.toPlainDate ^js yearmonth (js-obj "day" day))))

(defn date+time [date time]
  #?(:cljay (.atTime ^LocalDate date ^LocalTime time)
     :cljs (.toPlainDateTime ^js date time)))

(defn time+date [time date]
  (date+time date time))

(defn datetime+timezone [datetime timezone_id]
  #?(:cljay (.atZone ^LocalDateTime datetime (ZoneId/of timezone_id))
     :cljs (.toZonedDateTime ^js datetime timezone_id)))
