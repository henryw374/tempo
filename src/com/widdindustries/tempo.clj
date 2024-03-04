(ns
 com.widdindustries.tempo
 ""
 (:refer-clojure :exclude [min max > < >= <= >> <<])
 (:import
  [java.time
   Clock
   MonthDay
   ZoneId
   ZoneOffset
   Instant
   Duration
   Period
   DayOfWeek
   Month
   ZonedDateTime
   LocalTime
   LocalDateTime
   LocalDate
   Year
   YearMonth
   ZoneId
   OffsetDateTime
   OffsetTime]
  [java.time.temporal
   Temporal
   TemporalAmount
   TemporalUnit
   TemporalAccessor
   ChronoUnit
   ChronoField
   ValueRange]
  [java.util Date]))

(set! *warn-on-reflection* true)

(defn extend-all-cljs-protocols [])

(defn legacydate? [v] (instance? java.util.Date v))

(defn clock? [v] (instance? Clock v))

(defn period? [v] (instance? Period v))

(defn duration? [v] (instance? Duration v))

(defn instant? [v] (instance? Instant v))

(defn date? [v] (instance? LocalDate v))

(defn datetime? [v] (instance? LocalDateTime v))

(defn time? [v] (instance? LocalTime v))

(defn monthday? [v] (instance? MonthDay v))

(defn yearmonth? [v] (instance? YearMonth v))

(defn timezone? [v] (instance? ZoneId v))

(defn zdt? [v] (instance? ZonedDateTime v))

(defn
 clock-fixed
 [instant ^String zone-str]
 (Clock/fixed ^Instant instant (ZoneId/of zone-str)))

(defn
 clock-with-zone
 [^String timezone_id]
 (Clock/system (ZoneId/of timezone_id)))

(defn
 clock-system-default-zone
 "a ticking clock having the ambient zone. "
 []
 (Clock/systemDefaultZone))

(defn
 clock-offset-millis
 [clock offset-millis]
 (Clock/offset clock (Duration/ofMillis offset-millis)))

(defn timezone-now ([clock] (.getZone ^Clock clock)))

(defn legacydate->instant [d] (.toInstant ^java.util.Date d))

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__144548# p2__144549#] (greater p1__144548# p2__144549#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__144550# p2__144551#] (lesser p1__144550# p2__144551#))
  arg
  args))

(defn
 <
 ([_x] true)
 ([x y] (neg? (compare x y)))
 ([x y & more]
  (if
   (< x y)
   (if
    (next more)
    (recur y (first more) (next more))
    (< y (first more)))
   false)))

(defn
 <=
 ([_x] true)
 ([x y] (not (pos? (compare x y))))
 ([x y & more]
  (if
   (<= x y)
   (if
    (next more)
    (recur y (first more) (next more))
    (<= y (first more)))
   false)))

(defn
 >
 ([_x] true)
 ([x y] (pos? (compare x y)))
 ([x y & more]
  (if
   (> x y)
   (if
    (next more)
    (recur y (first more) (next more))
    (> y (first more)))
   false)))

(defn
 >=
 ([_x] true)
 ([x y] (not (neg? (compare x y))))
 ([x y & more]
  (if
   (>= x y)
   (if
    (next more)
    (recur y (first more) (next more))
    (>= y (first more)))
   false)))

(defn
 coincident?
 [start end event]
 (and (<= start event) (>= end event)))

(defprotocol
 Unit
 (unit-field [_])
 (unit-amount [_])
 (unit-accessor [_ x]))

(defprotocol Property (unit [_]) (field [_]))

(defprotocol HasTime (getFractional [_]) (setFractional [_ _]))

(defn
 -millisecond
 [f]
 (-> (getFractional f) (Duration/ofNanos) (.toMillisPart)))

(defn -microsecond [f] (-> (getFractional f) (/ 1000) long (mod 1000)))

(defn -nanosecond [f] (-> (getFractional f) (mod 1000)))

(extend-protocol
 HasTime
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
 (setFractional [x ^long t] (.with x ChronoField/NANO_OF_SECOND t)))

(def ^ValueRange sub-second-range (ValueRange/of 0 999))

(def
 nanoseconds-property
 (reify
  Object
  (toString [_] "nanoseconds-property")
  Property
  (unit [_] ChronoUnit/NANOS)
  (field
   [_]
   (reify
    java.time.temporal.TemporalField
    (rangeRefinedBy [_ _temporal] sub-second-range)
    (getFrom [_ temporal] (-nanosecond temporal))
    (adjustInto
     [_ temporal value]
     (.checkValidValue sub-second-range value nil)
     (let
      [fractional
       (getFractional temporal)
       millis+micros
       (-> fractional (/ 1000) long (* 1000))
       new-fractional
       (+ millis+micros value)]
      (-> temporal (setFractional new-fractional))))))))

(def
 microseconds-property
 (reify
  Object
  (toString [_] "microseconds-property")
  Property
  (unit [_] ChronoUnit/MICROS)
  (field
   [_]
   (reify
    java.time.temporal.TemporalField
    (rangeRefinedBy [_ _temporal] sub-second-range)
    (getFrom [_ temporal] (-microsecond temporal))
    (adjustInto
     [_ temporal value]
     (.checkValidValue sub-second-range value nil)
     (let
      [fractional
       (getFractional temporal)
       millis
       (-> fractional (/ 1000000) long (* 1000000))
       nanos
       (-> fractional (mod 1000))
       new-fractional
       (+ millis nanos (-> value (* 1000)))]
      (-> temporal (setFractional new-fractional))))))))

(def
 milliseconds-property
 (reify
  Object
  (toString [_] "milliseconds-property")
  Property
  (unit [_] ChronoUnit/MILLIS)
  (field
   [_]
   (reify
    java.time.temporal.TemporalField
    (rangeRefinedBy [_ _temporal] sub-second-range)
    (getFrom [_ temporal] (-millisecond temporal))
    (adjustInto
     [_ temporal value]
     (.checkValidValue sub-second-range value nil)
     (let
      [fractional
       (getFractional temporal)
       micros+nanos
       (-> fractional (mod 1000000))]
      (->
       temporal
       (setFractional (+ micros+nanos (-> value (* 1000000)))))))))))

(def
 seconds-property
 (reify
  Object
  (toString [_] "seconds-property")
  Property
  (unit [_] ChronoUnit/SECONDS)
  (field [_] ChronoField/SECOND_OF_MINUTE)))

(def
 minutes-property
 (reify
  Object
  (toString [_] "minutes-property")
  Property
  (unit [_] ChronoUnit/MINUTES)
  (field [_] ChronoField/MINUTE_OF_HOUR)))

(def
 hours-property
 (reify
  Object
  (toString [_] "hours-property")
  Property
  (unit [_] ChronoUnit/HOURS)
  (field [_] ChronoField/HOUR_OF_DAY)))

(def
 days-property
 (reify
  Object
  (toString [_] "days-property")
  Property
  (unit [_] ChronoUnit/DAYS)
  (field [_] ChronoField/DAY_OF_MONTH)))

(def
 months-property
 (reify
  Object
  (toString [_] "months-property")
  Property
  (unit [_] ChronoUnit/MONTHS)
  (field [_] ChronoField/MONTH_OF_YEAR)))

(def
 years-property
 (reify
  Object
  (toString [_] "years-property")
  Property
  (unit [_] ChronoUnit/YEARS)
  (field [_] ChronoField/YEAR)))

(def ^{:dynamic true} *block-non-commutative-operations* true)

(defn
 throw-if-set-months-or-years
 [temporal temporal-property]
 (when
  (and
   *block-non-commutative-operations*
   (contains? #{months-property years-property} temporal-property)
   (not (or (monthday? temporal) (yearmonth? temporal))))
  (throw
   (ex-info
    "shifting by years or months yields odd results depending on input. intead shift a year-month, then set non-yearmonth parts"
    {}))))

(defn
 with
 [temporal value property]
 (throw-if-set-months-or-years temporal property)
 (.with
  ^Temporal temporal
  ^{:tag TemporalField}
  (field property)
  ^long value))

(defn until [v1 v2 property] (.until ^Temporal v1 v2 (unit property)))

(defn
 >>
 ([temporal amount temporal-property]
  (throw-if-set-months-or-years temporal temporal-property)
  (.plus
   ^Temporal temporal
   amount
   ^{:tag TemporalUnit}
   (unit temporal-property))))

(defn
 <<
 ([temporal amount temporal-property]
  (throw-if-set-months-or-years temporal temporal-property)
  (.minus
   ^Temporal temporal
   amount
   ^{:tag TemporalUnit}
   (unit temporal-property))))

(defprotocol WeekDay (weekday-number [_]) (english-name [_]))

(extend-protocol
 WeekDay
 DayOfWeek
 (weekday-number [x] (.getValue x))
 (english-name [x] (str x)))

(def weekday-monday DayOfWeek/MONDAY)

(def weekday-tuesday DayOfWeek/TUESDAY)

(def weekday-wednesday DayOfWeek/WEDNESDAY)

(def weekday-thursday DayOfWeek/THURSDAY)

(def weekday-friday DayOfWeek/FRIDAY)

(def weekday-saturday DayOfWeek/SATURDAY)

(def weekday-sunday DayOfWeek/SUNDAY)

(def
 weekday-number->weekday
 {1 weekday-monday,
  2 weekday-tuesday,
  3 weekday-wednesday,
  4 weekday-thursday,
  5 weekday-friday,
  6 weekday-saturday,
  7 weekday-sunday})

(defprotocol JavaTruncateable (-truncate [_ unit]))

(extend-protocol
 JavaTruncateable
 ZonedDateTime
 (-truncate [zdt unit] (.truncatedTo zdt unit))
 LocalDateTime
 (-truncate [zdt unit] (.truncatedTo zdt unit))
 LocalTime
 (-truncate [zdt unit] (.truncatedTo zdt unit))
 Instant
 (-truncate [zdt unit] (.truncatedTo zdt unit)))

(defn truncate [temporal property] (-truncate temporal (unit property)))

(defn
 get-field
 [temporal property]
 (.get ^TemporalAccessor temporal (field property)))

^{:line 31, :column 9} (comment "accessors")

(defn zdt->year [^ZonedDateTime foo] (-> foo .getYear))

(defn
 datetime->day-of-week
 [^LocalDateTime foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn
 datetime->microsecond
 [^LocalDateTime foo]
 (-> foo (-> -microsecond)))

(defn datetime->second [^LocalDateTime foo] (-> foo .getSecond))

(defn zdt->date [^ZonedDateTime foo] (-> foo .toLocalDate))

(defn yearmonth->year [^YearMonth foo] (-> foo .getYear))

(defn monthday->day-of-month [^MonthDay foo] (-> foo .getDayOfMonth))

(defn date->year [^LocalDate foo] (-> foo .getYear))

(defn yearmonth->month [^YearMonth foo] (-> foo .getMonthValue))

(defn zdt->millisecond [^ZonedDateTime foo] (-> foo (-> -millisecond)))

(defn zdt->nanosecond [^ZonedDateTime foo] (-> foo (-> -nanosecond)))

(defn zdt->second [^ZonedDateTime foo] (-> foo .getSecond))

(defn datetime->minute [^LocalDateTime foo] (-> foo .getMinute))

(defn zdt->hour [^ZonedDateTime foo] (-> foo .getHour))

(defn zdt->instant [^ZonedDateTime foo] (-> foo .toInstant))

(defn monthday->month [^MonthDay foo] (-> foo .getMonthValue))

(defn
 datetime->day-of-month
 [^LocalDateTime foo]
 (-> foo .getDayOfMonth))

(defn date->month [^LocalDate foo] (-> foo .getMonthValue))

(defn datetime->date [^LocalDateTime foo] (-> foo .toLocalDate))

(defn zdt->microsecond [^ZonedDateTime foo] (-> foo (-> -microsecond)))

(defn zdt->day-of-month [^ZonedDateTime foo] (-> foo .getDayOfMonth))

(defn zdt->month [^ZonedDateTime foo] (-> foo .getMonthValue))

(defn datetime->year [^LocalDateTime foo] (-> foo .getYear))

(defn
 zdt->day-of-week
 [^ZonedDateTime foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn time->second [^LocalTime foo] (-> foo .getSecond))

(defn
 instant->legacydate
 [^Instant foo]
 (-> foo (-> (.toEpochMilli) (java.util.Date.))))

(defn date->day-of-month [^LocalDate foo] (-> foo .getDayOfMonth))

(defn
 datetime->nanosecond
 [^LocalDateTime foo]
 (-> foo (-> -nanosecond)))

(defn instant->epochmilli [^Instant foo] (-> foo .toEpochMilli))

(defn datetime->hour [^LocalDateTime foo] (-> foo .getHour))

(defn zdt->datetime [^ZonedDateTime foo] (-> foo .toLocalDateTime))

(defn time->microsecond [^LocalTime foo] (-> foo (-> -microsecond)))

(defn time->nanosecond [^LocalTime foo] (-> foo (-> -nanosecond)))

(defn time->minute [^LocalTime foo] (-> foo .getMinute))

(defn time->hour [^LocalTime foo] (-> foo .getHour))

(defn zdt->time [^ZonedDateTime foo] (-> foo .toLocalTime))

(defn
 zdt->timezone_id
 [^ZonedDateTime foo]
 (-> foo (-> (.getZone) (.getId))))

(defn
 date->day-of-week
 [^LocalDate foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn zdt->minute [^ZonedDateTime foo] (-> foo .getMinute))

(defn
 datetime->millisecond
 [^LocalDateTime foo]
 (-> foo (-> -millisecond)))

(defn datetime->month [^LocalDateTime foo] (-> foo .getMonthValue))

(defn time->millisecond [^LocalTime foo] (-> foo (-> -millisecond)))

(defn datetime->time [^LocalDateTime foo] (-> foo .toLocalTime))

^{:line 33, :column 9} (comment "parsers")

(defn timezone-parse [^java.lang.String foo] (ZoneId/of foo))

(defn instant-parse [^java.lang.String foo] (Instant/parse foo))

(defn zdt-parse [^java.lang.String foo] (ZonedDateTime/parse foo))

(defn datetime-parse [^java.lang.String foo] (LocalDateTime/parse foo))

(defn date-parse [^java.lang.String foo] (LocalDate/parse foo))

(defn time-parse [^java.lang.String foo] (LocalTime/parse foo))

(defn yearmonth-parse [^java.lang.String foo] (YearMonth/parse foo))

(defn monthday-parse [^java.lang.String foo] (MonthDay/parse foo))

^{:line 35, :column 9} (comment "nowers")

(defn zdt-now ([^java.time.Clock clock] (ZonedDateTime/now clock)))

(defn datetime-now ([^java.time.Clock clock] (LocalDateTime/now clock)))

(defn date-now ([^java.time.Clock clock] (LocalDate/now clock)))

(defn time-now ([^java.time.Clock clock] (LocalTime/now clock)))

(defn instant-now ([^java.time.Clock clock] (Instant/now clock)))

(defn yearmonth-now ([^java.time.Clock clock] (YearMonth/now clock)))

(defn monthday-now ([^java.time.Clock clock] (MonthDay/now clock)))

^{:line 37, :column 9} (comment "constructors")

(defn
 time-from
 [thing]
 (let
  [hour
   (get thing :hour 0)
   minute
   (get thing :minute 0)
   second
   (get thing :second 0)
   milli
   (get thing :millisecond 0)
   micro
   (get thing :microsecond 0)
   nano
   (get thing :nanosecond 0)]
  (LocalTime/of
   ^int hour
   ^int minute
   ^int second
   ^{:tag int}
   (+ (* milli 1000000) (* micro 1000) nano))))

(defn
 date-from
 [thing]
 (let
  [year
   (or (some-> (get thing :yearmonth) yearmonth->year) (:year thing))
   month
   (or
    (some-> (get thing :yearmonth) yearmonth->month)
    (some-> (get thing :monthday) monthday->month)
    (:month thing))
   day
   (or
    (some-> (get thing :monthday) monthday->day-of-month)
    (get thing :day-of-month))]
  (LocalDate/of ^int year ^int month ^int day)))

(defn
 yearmonth-from
 [{:keys [year month]}]
 (YearMonth/of ^int year ^int month))

(defn
 monthday-from
 [{:keys [month day-of-month]}]
 (MonthDay/of ^int month ^int day-of-month))

(defn
 datetime-from
 [thing]
 (let
  [date
   (or (get thing :date) (date-from thing))
   time
   (or (get thing :time) (time-from thing))]
  (LocalDateTime/of ^LocalDate date ^LocalTime time)))

(defn
 zdt-from
 [thing]
 (let
  [instant
   (get thing :instant)
   ldt
   (or
    instant
    (some-> (get thing :zdt) zdt->datetime)
    (get thing :datetime)
    (datetime-from thing))
   zone
   (get thing :timezone_id)]
  (if
   instant
   (ZonedDateTime/ofInstant instant (timezone-parse zone))
   (ZonedDateTime/of
    ^LocalDateTime ldt
    ^{:tag ZoneId}
    (timezone-parse zone)))))

(defn
 instant-from
 [thing]
 (or
  (some-> (get thing :epochmilli) (Instant/ofEpochMilli))
  (when-let [d (get thing :legacydate)] (.toInstant ^Date d))
  (some-> (or (get thing :zdt) (zdt-from thing)) (zdt->instant))))

