(ns com.widdindustries.gen.gen-in.tempo)

(ns com.widdindustries.tempo
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
             [goog.object])))

#?(:cljay (set! *warn-on-reflection* true))

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
   (defn timezone-parse [x]
     (ZoneId/of x)))

(comment "after-graph")

(defn extend-all-cljs-protocols
  "in cljs envs, this makes `=`, `compare` and `hash` work on the value of Temporal entities.
  It is optional, so that if this behaviour is not required, the resulting build size can be reduced. 
  "
  []
  #?(:cljs
     (cljs-protocols/extend-all)))

(defn legacydate? [v]
  #?(:cljs (instance? js/Date v)
     :cljay (instance? java.util.Date v)
     ))

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
#_(defn timezone? [v] #?(:cljay (instance? ZoneId v)
                       :cljs (instance? entities/timezone v)))
(defn zdt? [v] #?(:cljay (instance? ZonedDateTime v)
                  :cljs (instance? entities/zdt v)))

#?(:cljay
   (defn- java-time-clock 
     "returns a partial implementation of java.time.Clock sufficient for all the java.time 'now' methods"
     [instant-fn timezone_id-fn]
     (proxy [java.time.Clock] []
       (getZone [] (java.time.ZoneId/of (timezone_id-fn)))
       (instant [] (instant-fn)))))

;; construction of clocks
(defn clock-system-default-zone
  "a ticking clock having the ambient zone. "
  []
  #?(:cljay (Clock/systemDefaultZone)
     :cljs js/Temporal.Now))

(defn clock-fixed 
  "create a stopped clock"
  ([^ZonedDateTime zdt]
   #?(:cljay (Clock/fixed (.toInstant zdt)   (.getZone zdt))
      :cljs (clock/clock (constantly (.toInstant zdt)) (constantly (.-timeZoneId zdt)))))
  ([^Instant instant ^String zone-str]
   #?(:cljay (Clock/fixed instant (ZoneId/of zone-str))
      :cljs (clock/clock (constantly instant) (constantly zone-str)))))

(defn clock-with-timezone_id 
  "ticking clock in given timezone_id" 
  [^String timezone_id]
  #?(:cljay (Clock/system (ZoneId/of timezone_id))
     :cljs (clock/clock js/Temporal.Now.instant (constantly timezone_id))))

(defn clock-offset-millis 
  "offset an existing clock by offset-millis"
  [a-clock offset-millis]
  #?(:cljay (Clock/offset a-clock (Duration/ofMillis offset-millis))
     :cljs (clock/clock
             (fn [] (.add (.instant ^js a-clock) (js-obj "milliseconds" offset-millis)))
             (constantly (clock/timezone_id a-clock)))))

(defn clock [instant-fn timezone_id-fn]
  #?(:cljay (java-time-clock instant-fn timezone_id-fn)
     :cljs (clock/clock instant-fn timezone_id-fn)))

(defn clock-zdt-atom
  "create a clock which will dereference the zdt-atom.
  
  The caller must first construct the atom and by keeping a reference to it,
   may change its value and therefore the value of the clock.
  "
  [zdt-atom]
  (clock
    (fn get-instant []
      (zdt->instant @zdt-atom))
    (fn get-zone []
      (zdt->timezone_id @zdt-atom))))

(defn timezone_id-now
  ([clock] #?(:cljay (str (.getZone ^Clock clock))
              :cljs (clock/timezone_id clock))))

(defn legacydate->instant [d]
  #?(:cljay (.toInstant ^java.util.Date d)
     :cljs (.toTemporalInstant ^js d)))

(defn instant->zdt-in-UTC [instant]
  #?(:cljay (ZonedDateTime/ofInstant instant (ZoneId/of "UTC"))
     :cljs (.toZonedDateTimeISO ^js instant "UTC")))

(defn- greater [x y]
  (if (neg? (compare x y)) y x))

(defn max
  "Find the latest of the given arguments. Callers should ensure that no
  argument is nil."
  [arg & args]
  (assert (every? some? (cons arg args)))
  (reduce #(greater %1 %2) arg args))

(defn- lesser [x y]
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
             "see guardrails section at https://github.com/henryw374/tempo?tab=readme-ov-file#guardrails"
             {}))))

(defn throw-if-months-or-years-in-amount [temporal temporal-amount]
  (when
    (and *block-non-commutative-operations*
      (not (or (monthday? temporal) (yearmonth? temporal)))
      #?(:cljay (and (instance? Period temporal-amount)
                  (or
                    (not (zero? (.getYears ^Period temporal-amount)))
                    (not (zero? (.getMonths ^Period temporal-amount)))))
         :cljs (or
                 (not (zero? (.-years ^js temporal-amount)))
                 (not (zero? (.-months ^js temporal-amount))))))
    (throw (ex-info
             "see guardrails section at https://github.com/henryw374/tempo?tab=readme-ov-file#guardrails"
             {}))))

(defn with 
  "from temporal arg, derive a new temporal object with property field set to value
  (t/with date 3 t/days-property) "
  [temporal value property]
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
  "move a temporal forward by an amount"
  ([temporal temporal-amount]
     (throw-if-months-or-years-in-amount temporal temporal-amount)
     #?(:cljay (.plus ^Temporal temporal ^TemporalAmount temporal-amount)
        :cljs (.add ^js temporal temporal-amount)))
  ([temporal amount temporal-property]
   (throw-if-set-months-or-years temporal temporal-property)
   #?(:cljay (.plus ^Temporal temporal amount ^TemporalUnit (unit temporal-property))
      :cljs (.add ^js temporal (js-obj (str temporal-property "s") amount)))))

(defn <<
  "move a temporal backward by an amount"
  ([temporal temporal-amount]
     (throw-if-months-or-years-in-amount temporal temporal-amount)
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

(defn truncate 
  "zero property field and below of temporal"
  [temporal property]
  #?(:cljay (-truncate temporal (unit property))
     :cljs (.round ^js temporal 
             (js-obj "smallestUnit" property "roundingMode" "trunc"))))

(defn get-field [temporal property]
  #?(:cljay (.get ^TemporalAccessor temporal (field property))
     :cljs (goog.object/get temporal property)))

(defn yearmonth+day-at-end-of-month 
  "create a date having last day of month" 
  [ym]
  #?(:cljay (.atEndOfMonth ^YearMonth ym)
     :cljs (.toPlainDate ^js ym (js-obj "day" (.-daysInMonth ^js ym)))))

(defn monthday+year 
  "create a date"
  [monthday year]
  #?(:cljay (.atYear ^MonthDay monthday ^int year)
     :cljs (.toPlainDate ^js monthday (js-obj "year" year))))

(defn yearmonth+day
  "create a date"
  [yearmonth day]
  #?(:cljay (.atDay ^YearMonth yearmonth ^int day)
     :cljs (.toPlainDate ^js yearmonth (js-obj "day" day))))

(defn date+time
  "create a datetime"
  [date time]
  #?(:cljay (.atTime ^LocalDate date ^LocalTime time)
     :cljs (.toPlainDateTime ^js date time)))

(defn time+date
  "create a datetime" [time date]
  (date+time date time))

(defn datetime+timezone_id
  "create a zdt"
  [datetime timezone_id]
  #?(:cljay (.atZone ^LocalDateTime datetime (ZoneId/of timezone_id))
     :cljs (.toZonedDateTime ^js datetime timezone_id)))

(defn instant+timezone_id
  "create a zdt" [instant timezone_id]
  #?(:cljay (.atZone ^Instant instant (ZoneId/of timezone_id))
     :cljs (.toZonedDateTimeISO ^js instant timezone_id)))

(defn epochmilli->instant [milli]
  #?(:cljay (Instant/ofEpochMilli milli)
     :cljs (js/Temporal.Instant.fromEpochMilliseconds milli)))

(defn date-next-or-same-weekday [date desired-dow-number]
  (let [curr-day-of-week (date->day-of-week date)]
    (>> date
      (-> (abs (- curr-day-of-week (+ 7 desired-dow-number)))
          (mod 7))
      days-property)))

(defn date-prev-or-same-weekday [date desired-dow-number]
  (let [curr-day-of-week (date->day-of-week date)]
    (<< date
      (-> (abs (- (+ 7 curr-day-of-week) desired-dow-number))
          (mod 7))
      days-property)))