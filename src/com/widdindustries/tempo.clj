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
  [java.time.temporal Temporal TemporalAmount TemporalUnit ChronoUnit]
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

(defn zone-system-default [] (ZoneId/systemDefault))

(defn
 clock-fixed
 [instant zone]
 (Clock/fixed ^Instant instant ^ZoneId zone))

(defn
 clock-system-default-zone
 "a ticking clock having the ambient zone"
 []
 (Clock/systemDefaultZone))

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__81481# p2__81482#] (greater p1__81481# p2__81482#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__81483# p2__81484#] (lesser p1__81483# p2__81484#))
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

(def nanos-unit ChronoUnit/NANOS)

(def micros-unit ChronoUnit/MICROS)

(def millis-unit ChronoUnit/MILLIS)

(def seconds-unit ChronoUnit/SECONDS)

(def minutes-unit ChronoUnit/MINUTES)

(def hours-unit ChronoUnit/HOURS)

(def days-unit ChronoUnit/DAYS)

(def months-unit ChronoUnit/MONTHS)

(def years-unit ChronoUnit/YEARS)

(defn until [v1 v2 unit] (.until ^Temporal v1 v2 unit))

(defn
 >>
 ([temporal temporal-amount]
  (.plus ^Temporal temporal ^TemporalAmount temporal-amount))
 ([temporal amount temporal-unit]
  (.plus ^Temporal temporal amount ^TemporalUnit temporal-unit)))

(defn
 <<
 ([temporal temporal-amount]
  (.minus ^Temporal temporal ^TemporalAmount temporal-amount))
 ([temporal amount temporal-unit]
  (.minus ^Temporal temporal amount ^TemporalUnit temporal-unit)))

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

(defn monthday->month [^MonthDay foo] (-> foo ".getMonthValue"))

(defn datetime->year [^LocalDateTime foo] (-> foo .getYear))

(defn datetime->month [^LocalDateTime foo] (-> foo ".getMonthValue"))

(defn zdt->year [^ZonedDateTime foo] (-> foo .getYear))

(defn date->month [^LocalDate foo] (-> foo ".getMonthValue"))

(defn yearmonth->month [^YearMonth foo] (-> foo ".getMonthValue"))

(defn datetime->hour [^LocalDateTime foo] (-> foo .getHour))

(defn yearmonth->year [^YearMonth foo] (-> foo .getYear))

(defn date->day-of-month [^LocalDate foo] (-> foo .getDayOfMonth))

(defn zdt->day-of-month [^ZonedDateTime foo] (-> foo .getDayOfMonth))

(defn zdt->hour [^ZonedDateTime foo] (-> foo .getHour))

(defn zdt->instant [^ZonedDateTime foo] (-> foo .toInstant))

(defn zdt->nano [^ZonedDateTime foo] (-> foo .getNano))

(defn
 zdt->day-of-week
 [^ZonedDateTime foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn zdt->month [^ZonedDateTime foo] (-> foo ".getMonthValue"))

(defn zdt->minute [^ZonedDateTime foo] (-> foo .getMinute))

(defn
 datetime->day-of-month
 [^LocalDateTime foo]
 (-> foo .getDayOfMonth))

(defn date->year [^LocalDate foo] (-> foo .getYear))

(defn datetime->date [^LocalDateTime foo] (-> foo .toLocalDate))

(defn
 zdt->timezone
 [^ZonedDateTime foo]
 (-> foo (-> (.getZone) (.getId))))

(defn zdt->second [^ZonedDateTime foo] (-> foo .getSecond))

(defn instant->epochmilli [^Instant foo] (-> foo ".toEpochMilli"))

(defn time->second [^LocalTime foo] (-> foo .getSecond))

(defn
 date->day-of-week
 [^LocalDate foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn zdt->datetime [^ZonedDateTime foo] (-> foo .toLocalDateTime))

(defn zdt->time [^ZonedDateTime foo] (-> foo .toLocalTime))

(defn datetime->minute [^LocalDateTime foo] (-> foo .getMinute))

(defn instant->legacydate [^Instant foo] (-> foo .getLegacydate))

(defn
 datetime->day-of-week
 [^LocalDateTime foo]
 (-> foo (-> (.getDayOfWeek) (.getValue))))

(defn time->nano [^LocalTime foo] (-> foo .getNano))

(defn time->minute [^LocalTime foo] (-> foo .getMinute))

(defn time->hour [^LocalTime foo] (-> foo .getHour))

(defn datetime->nano [^LocalDateTime foo] (-> foo .getNano))

(defn datetime->time [^LocalDateTime foo] (-> foo .toLocalTime))

(defn monthday->day-of-month [^MonthDay foo] (-> foo .getDayOfMonth))

^{:line 33, :column 9} (comment "parsers")

(defn datetime-parse [^java.lang.String foo] (LocalDateTime/parse foo))

(defn time-parse [^java.lang.String foo] (LocalTime/parse foo))

(defn zdt-parse [^java.lang.String foo] (ZonedDateTime/parse foo))

(defn date-parse [^java.lang.String foo] (LocalDate/parse foo))

(defn monthday-parse [^java.lang.String foo] (MonthDay/parse foo))

(defn yearmonth-parse [^java.lang.String foo] (YearMonth/parse foo))

(defn instant-parse [^java.lang.String foo] (Instant/parse foo))

(defn timezone-parse [^java.lang.String foo] (ZoneId/of foo))

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
 monthday-now
 ([] (MonthDay/now))
 ([^java.time.Clock clock] (MonthDay/now clock)))

(defn
 yearmonth-now
 ([] (YearMonth/now))
 ([^java.time.Clock clock] (YearMonth/now clock)))

(defn
 instant-now
 ([] (Instant/now))
 ([^java.time.Clock clock] (Instant/now clock)))

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
   nano
   (get thing :nano 0)]
  (LocalTime/of ^int hour ^int minute ^int second ^int nano)))

(defn
 date-from
 [thing]
 (let
  [year
   (or (some-> (get thing :yearmonth) yearmonth->year) (:year thing))
   month
   (or
    (some-> (get thing :yearmonth) yearmonth->month)
    (-> (get thing :monthday) monthday->month)
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
   (get thing :timezone)]
  (ZonedDateTime/of ^LocalDateTime ldt ^ZoneId zone)))

(defn
 instant-from
 [thing]
 (or
  (some-> (get thing :epochmilli) (Instant/ofEpochMilli))
  (when-let [d (get thing :legacydate)] (.toInstant ^Date d))
  (some-> (or (get thing :zdt) (zdt-from thing)) (zdt->instant))))

