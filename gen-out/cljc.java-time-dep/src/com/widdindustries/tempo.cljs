(ns
 com.widdindustries.tempo
 ""
 (:refer-clojure :exclude [min max > < >= <= >> <<])
 (:require
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
  [java.time
   :refer
   [Clock
    ZoneId
    ZoneOffset
    Instant
    Duration
    Period
    DayOfWeek
    Month
    ZonedDateTime
    LocalTime
    MonthDay
    LocalDateTime
    LocalDate
    Year
    YearMonth
    OffsetDateTime
    OffsetTime]]
  [cljs.java-time.extend-eq-and-compare]))

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

(defn
 >>
 [temporal temporal-amount]
 (.plus ^Temporal temporal ^TemporalAmount temporal-amount))

(defn
 <<
 [temporal temporal-amount]
 (.minus ^Temporal temporal ^TemporalAmount temporal-amount))

(defn zone-system-default [] (cljc.java-time.zone-id/system-default))

(defn duration->negated [d] (.negated ^Duration d))

(defn duration-negative? [d] (.isNegative ^Duration d))

(defn
 clock-fixed
 [instant zone]
 (cljc.java-time.clock/fixed instant zone))

(defn
 clock-system-default-zone
 []
 (cljc.java-time.clock/system-default-zone))

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__11133# p2__11134#] (greater p1__11133# p2__11134#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__11135# p2__11136#] (lesser p1__11135# p2__11136#))
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

(comment "accessors")

(defn yearmonth->month [^java.time.YearMonth foo] (.getMonthValue foo))

(defn
 datetime->second
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-second foo))

(defn
 zdt->date
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/to-local-date foo))

(defn
 monthday->day-of-month
 [^java.time.MonthDay foo]
 (cljc.java-time.month-day/get-day-of-month foo))

(defn
 datetime->year
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-year foo))

(defn
 datetime->month
 [^java.time.LocalDateTime foo]
 (.getMonthValue foo))

(defn
 date->year
 [^java.time.LocalDate foo]
 (cljc.java-time.local-date/get-year foo))

(defn monthday->month [^java.time.MonthDay foo] (.getMonthValue foo))

(defn date->month [^java.time.LocalDate foo] (.getMonthValue foo))

(defn
 zdt->year
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-year foo))

(defn
 datetime->hour
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-hour foo))

(defn
 zdt->hour
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-hour foo))

(defn
 zdt->instant
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/to-instant foo))

(defn
 zdt->nano
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-nano foo))

(defn
 yearmonth->year
 [^java.time.YearMonth foo]
 (cljc.java-time.year-month/get-year foo))

(defn zdt->timezone [^java.time.ZonedDateTime foo] (.getZone foo))

(defn
 zdt->minute
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-minute foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonthValue foo))

(defn
 datetime->date
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/to-local-date foo))

(defn
 date->day-of-month
 [^java.time.LocalDate foo]
 (cljc.java-time.local-date/get-day-of-month foo))

(defn
 zdt->second
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-second foo))

(defn
 datetime->day-of-month
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-day-of-month foo))

(defn instant->epochmilli [^java.time.Instant foo] (.toEpochMilli foo))

(defn
 time->second
 [^java.time.LocalTime foo]
 (cljc.java-time.local-time/get-second foo))

(defn
 zdt->datetime
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/to-local-date-time foo))

(defn
 zdt->day-of-month
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-day-of-month foo))

(defn
 zdt->time
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/to-local-time foo))

(defn
 datetime->minute
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-minute foo))

(defn
 time->nano
 [^java.time.LocalTime foo]
 (cljc.java-time.local-time/get-nano foo))

(defn
 time->minute
 [^java.time.LocalTime foo]
 (cljc.java-time.local-time/get-minute foo))

(defn
 time->hour
 [^java.time.LocalTime foo]
 (cljc.java-time.local-time/get-hour foo))

(defn
 datetime->nano
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-nano foo))

(defn
 datetime->time
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/to-local-time foo))

(comment "parsers")

(defn
 yearmonth-parse
 [^java.lang.String foo]
 (cljc.java-time.year-month/parse foo))

(defn
 datetime-parse
 [^java.lang.String foo]
 (cljc.java-time.local-date-time/parse foo))

(defn
 time-parse
 [^java.lang.String foo]
 (cljc.java-time.local-time/parse foo))

(defn
 zdt-parse
 [^java.lang.String foo]
 (cljc.java-time.zoned-date-time/parse foo))

(defn
 date-parse
 [^java.lang.String foo]
 (cljc.java-time.local-date/parse foo))

(defn
 monthday-parse
 [^java.lang.String foo]
 (cljc.java-time.month-day/parse foo))

(defn
 instant-parse
 [^java.lang.String foo]
 (cljc.java-time.instant/parse foo))

(defn
 timezone-parse
 [^java.lang.String foo]
 (cljc.java-time.zone-id/of foo))

(comment "nowers")

(defn
 yearmonth-now
 ([] (cljc.java-time.year-month/now))
 ([^java.time.Clock clock] (cljc.java-time.year-month/now clock)))

(defn
 datetime-now
 ([] (cljc.java-time.local-date-time/now))
 ([^java.time.Clock clock] (cljc.java-time.local-date-time/now clock)))

(defn
 time-now
 ([] (cljc.java-time.local-time/now))
 ([^java.time.Clock clock] (cljc.java-time.local-time/now clock)))

(defn
 zdt-now
 ([] (cljc.java-time.zoned-date-time/now))
 ([^java.time.Clock clock] (cljc.java-time.zoned-date-time/now clock)))

(defn
 date-now
 ([] (cljc.java-time.local-date/now))
 ([^java.time.Clock clock] (cljc.java-time.local-date/now clock)))

(defn
 monthday-now
 ([] (cljc.java-time.month-day/now))
 ([^java.time.Clock clock] (cljc.java-time.month-day/now clock)))

(defn
 instant-now
 ([] (cljc.java-time.instant/now))
 ([^java.time.Clock clock] (cljc.java-time.instant/now clock)))

(comment "constructors")

(defn
 time-from
 [thing]
 (let
  [^int hour
   (get thing :hour 0)
   ^int minute
   (get thing :minute 0)
   ^int second
   (get thing :second 0)
   ^int nano
   (get thing :nano 0)]
  (cljc.java-time.local-time/of hour minute second nano)))

(defn
 date-from
 [thing]
 (let
  [year
   (or (-> (get thing :yearmonth) yearmonth->year) (:year thing))
   month
   (or
    (-> (get thing :yearmonth) yearmonth->month)
    (-> (get thing :monthday) monthday->month)
    (:month thing))
   day
   (or
    (-> (get thing :monthday) monthday->day-of-month)
    (get thing :day-of-month))]
  (cljc.java-time.local-date/of ^int year ^int month ^int day)))

(defn
 datetime-from
 [thing]
 (let
  [date
   (or (get thing :date) (date-from thing))
   time
   (or (get thing :time) (time-from thing))]
  (cljc.java-time.local-date-time/of ^LocalDate date ^LocalTime time)))

(defn
 zdt-from
 [thing]
 (let
  [ldt
   (or (get thing :datetime) (datetime-from thing))
   zone
   (get thing :timezone)]
  (cljc.java-time.zoned-date-time/of ^LocalDateTime ldt ^ZoneId zone)))

