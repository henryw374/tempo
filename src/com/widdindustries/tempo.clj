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
   ChronoUnit
   ChronoField
   ValueRange]
  [java.util Date]))

(set! *warn-on-reflection* true)

(defn extend-all-cljs-protocols [])

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

(defn timezone-system-default [] (ZoneId/systemDefault))

(defn
 clock-fixed
 [instant ^String zone-str]
 (Clock/fixed ^Instant instant (ZoneId/of zone-str)))

(defn
 clock-system-default-zone
 "a ticking clock having the ambient zone. "
 []
 (Clock/systemDefaultZone))

(defn
 clock-offset-millis
 [clock offset-millis]
 (Clock/offset clock (Duration/ofMillis offset-millis)))

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__75887# p2__75888#] (greater p1__75887# p2__75888#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__75889# p2__75890#] (lesser p1__75889# p2__75890#))
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

(defprotocol
 Unit
 (unit-field [_])
 (unit-amount [_])
 (unit-accessor [_ x]))

(defprotocol Property (unit [_]) (field [_]))

(defprotocol HasTime (getFractional [_]) (setFractional [_ _]))

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
 nanos-property
 (reify
  Property
  (unit [_] ChronoUnit/NANOS)
  (field
   [_]
   (reify
    java.time.temporal.TemporalField
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
 micros-property
 (reify
  Property
  (unit [_] ChronoUnit/MICROS)
  (field
   [_]
   (reify
    java.time.temporal.TemporalField
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
 millis-property
 (reify
  Property
  (unit [_] ChronoUnit/MILLIS)
  (field
   [_]
   (reify
    java.time.temporal.TemporalField
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
  Property
  (unit [_] ChronoUnit/SECONDS)
  (field [_] ChronoField/SECOND_OF_MINUTE)))

(def
 minutes-property
 (reify
  Property
  (unit [_] ChronoUnit/MINUTES)
  (field [_] ChronoField/MINUTE_OF_HOUR)))

(def
 hours-property
 (reify
  Property
  (unit [_] ChronoUnit/HOURS)
  (field [_] ChronoField/HOUR_OF_DAY)))

(def
 days-property
 (reify
  Property
  (unit [_] ChronoUnit/DAYS)
  (field [_] ChronoField/DAY_OF_MONTH)))

(def
 months-property
 (reify
  Property
  (unit [_] ChronoUnit/MONTHS)
  (field [_] ChronoField/MONTH_OF_YEAR)))

(def
 years-property
 (reify
  Property
  (unit [_] ChronoUnit/YEARS)
  (field [_] ChronoField/YEAR)))

(defn
 with
 [temporal value property]
 (.with
  ^Temporal temporal
  ^{:tag TemporalField}
  (field property)
  ^long value))

(defn until [v1 v2 property] (.until ^Temporal v1 v2 (unit property)))

(defn
 >>
 ([temporal temporal-amount]
  (.plus ^Temporal temporal ^TemporalAmount temporal-amount))
 ([temporal amount temporal-property]
  (.plus
   ^Temporal temporal
   amount
   ^{:tag TemporalUnit}
   (unit temporal-property))))

(defn
 <<
 ([temporal temporal-amount]
  (.minus ^Temporal temporal ^TemporalAmount temporal-amount))
 ([temporal amount temporal-property]
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

^{:line 31, :column 9} (comment "accessors")

(defn datetime->second [^LocalDateTime foo] (-> foo .getSecond))

(defn zdt->date [^ZonedDateTime foo] (-> foo .toLocalDate))

(defn datetime->year [^LocalDateTime foo] (-> foo .getYear))

(defn datetime->month [^LocalDateTime foo] (-> foo .getMonthValue))

(defn
 time->millisecond
 [^LocalTime foo]
 (-> foo (-> (.getNano) (Duration/ofNanos) (.toMillisPart))))

(defn zdt->year [^ZonedDateTime foo] (-> foo .getYear))

(defn
 zdt->timezone_id
 [^ZonedDateTime foo]
 (-> foo (-> (.getZone) (.getId))))

(defn datetime->hour [^LocalDateTime foo] (-> foo .getHour))

(defn
 zdt->nanosecond
 [^ZonedDateTime foo]
 (-> foo (-> (.getNano) (mod 1000))))

(defn date->year [^LocalDate foo] (-> foo .getYear))

(defn date->day-of-month [^LocalDate foo] (-> foo .getDayOfMonth))

(defn date->month [^LocalDate foo] (-> foo .getMonthValue))

(defn zdt->hour [^ZonedDateTime foo] (-> foo .getHour))

(defn zdt->instant [^ZonedDateTime foo] (-> foo .toInstant))

(defn
 zdt->day-of-week
 [^ZonedDateTime foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn
 time->nanosecond
 [^LocalTime foo]
 (-> foo (-> (.getNano) (mod 1000))))

(defn zdt->minute [^ZonedDateTime foo] (-> foo .getMinute))

(defn
 datetime->day-of-month
 [^LocalDateTime foo]
 (-> foo .getDayOfMonth))

(defn datetime->date [^LocalDateTime foo] (-> foo .toLocalDate))

(defn
 datetime->nanosecond
 [^LocalDateTime foo]
 (-> foo (-> (.getNano) (mod 1000))))

(defn zdt->second [^ZonedDateTime foo] (-> foo .getSecond))

(defn zdt->month [^ZonedDateTime foo] (-> foo .getMonthValue))

(defn yearmonth->year [^YearMonth foo] (-> foo .getYear))

(defn
 zdt->millisecond
 [^ZonedDateTime foo]
 (-> foo (-> (.getNano) (Duration/ofNanos) (.toMillisPart))))

(defn instant->epochmilli [^Instant foo] (-> foo .toEpochMilli))

(defn monthday->month [^MonthDay foo] (-> foo .getMonthValue))

(defn time->second [^LocalTime foo] (-> foo .getSecond))

(defn
 time->microsecond
 [^LocalTime foo]
 (-> foo (-> (.getNano) (/ 1000) long (mod 1000))))

(defn
 date->day-of-week
 [^LocalDate foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn zdt->datetime [^ZonedDateTime foo] (-> foo .toLocalDateTime))

(defn
 datetime->microsecond
 [^LocalDateTime foo]
 (-> foo (-> (.getNano) (/ 1000) long (mod 1000))))

(defn yearmonth->month [^YearMonth foo] (-> foo .getMonthValue))

(defn zdt->time [^ZonedDateTime foo] (-> foo .toLocalTime))

(defn datetime->minute [^LocalDateTime foo] (-> foo .getMinute))

(defn instant->legacydate [^Instant foo] (-> foo .getLegacydate))

(defn
 datetime->day-of-week
 [^LocalDateTime foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn
 datetime->millisecond
 [^LocalDateTime foo]
 (-> foo (-> (.getNano) (Duration/ofNanos) (.toMillisPart))))

(defn zdt->day-of-month [^ZonedDateTime foo] (-> foo .getDayOfMonth))

(defn time->minute [^LocalTime foo] (-> foo .getMinute))

(defn time->hour [^LocalTime foo] (-> foo .getHour))

(defn
 zdt->microsecond
 [^ZonedDateTime foo]
 (-> foo (-> (.getNano) (/ 1000) long (mod 1000))))

(defn monthday->day-of-month [^MonthDay foo] (-> foo .getDayOfMonth))

(defn datetime->time [^LocalDateTime foo] (-> foo .toLocalTime))

^{:line 33, :column 9} (comment "parsers")

(defn timezone-parse [^java.lang.String foo] (ZoneId/of foo))

(defn instant-parse [^java.lang.String foo] (Instant/parse foo))

(defn zdt-parse [^java.lang.String foo] (ZonedDateTime/parse foo))

(defn datetime-parse [^java.lang.String foo] (LocalDateTime/parse foo))

(defn time-parse [^java.lang.String foo] (LocalTime/parse foo))

(defn date-parse [^java.lang.String foo] (LocalDate/parse foo))

(defn yearmonth-parse [^java.lang.String foo] (YearMonth/parse foo))

(defn monthday-parse [^java.lang.String foo] (MonthDay/parse foo))

^{:line 35, :column 9} (comment "nowers")

(defn
 datetime-now
 ([] (LocalDateTime/now))
 ([^java.time.Clock clock] (LocalDateTime/now clock)))

(defn
 time-now
 ([] (LocalTime/now))
 ([^java.time.Clock clock] (LocalTime/now clock)))

(defn
 zdt-now
 ([] (ZonedDateTime/now))
 ([^java.time.Clock clock] (ZonedDateTime/now clock)))

(defn
 date-now
 ([] (LocalDate/now))
 ([^java.time.Clock clock] (LocalDate/now clock)))

(defn
 instant-now
 ([] (Instant/now))
 ([^java.time.Clock clock] (Instant/now clock)))

(defn
 yearmonth-now
 ([] (YearMonth/now))
 ([^java.time.Clock clock] (YearMonth/now clock)))

(defn
 monthday-now
 ([] (MonthDay/now))
 ([^java.time.Clock clock] (MonthDay/now clock)))

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
  [ldt
   (or (get thing :datetime) (datetime-from thing))
   zone
   (get thing :timezone_id)]
  (ZonedDateTime/of
   ^LocalDateTime ldt
   ^{:tag ZoneId}
   (timezone-parse zone))))

(defn
 instant-from
 [thing]
 (or
  (some-> (get thing :epochmilli) (Instant/ofEpochMilli))
  (when-let [d (get thing :legacydate)] (.toInstant ^Date d))
  (some-> (or (get thing :zdt) (zdt-from thing)) (zdt->instant))))

