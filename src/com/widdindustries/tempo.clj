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
  [java.time.temporal Temporal TemporalAmount]))

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

(defn
 >>
 [temporal temporal-amount]
 (.plus ^Temporal temporal ^TemporalAmount temporal-amount))

(defn
 <<
 [temporal temporal-amount]
 (.minus ^Temporal temporal ^TemporalAmount temporal-amount))

(defn zone-system-default [] (ZoneId/systemDefault))

(defn duration->negated [d] (.negated ^Duration d))

(defn duration-negative? [d] (.isNegative ^Duration d))

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
  (fn* [p1__55766# p2__55767#] (greater p1__55766# p2__55767#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__55768# p2__55769#] (lesser p1__55768# p2__55769#))
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

^{:line 30, :column 9} (comment "accessors")

(defn datetime->second [^LocalDateTime foo] (.getSecond foo))

(defn zdt->date [^ZonedDateTime foo] (.toLocalDate foo))

(defn datetime->year [^LocalDateTime foo] (.getYear foo))

(defn datetime->month [^LocalDateTime foo] (.getMonthValue foo))

(defn zdt->year [^ZonedDateTime foo] (.getYear foo))

(defn monthday->day-of-month [^MonthDay foo] (.getDayOfMonth foo))

(defn datetime->hour [^LocalDateTime foo] (.getHour foo))

(defn zdt->month [^ZonedDateTime foo] (.getMonthValue foo))

(defn date->day-of-month [^LocalDate foo] (.getDayOfMonth foo))

(defn date->month [^LocalDate foo] (.getMonthValue foo))

(defn zdt->hour [^ZonedDateTime foo] (.getHour foo))

(defn zdt->instant [^ZonedDateTime foo] (.toInstant foo))

(defn zdt->nano [^ZonedDateTime foo] (.getNano foo))

(defn yearmonth->month [^YearMonth foo] (.getMonthValue foo))

(defn zdt->minute [^ZonedDateTime foo] (.getMinute foo))

(defn datetime->day-of-month [^LocalDateTime foo] (.getDayOfMonth foo))

(defn datetime->date [^LocalDateTime foo] (.toLocalDate foo))

(defn zdt->second [^ZonedDateTime foo] (.getSecond foo))

(defn instant->epochmilli [^Instant foo] (.toEpochMilli foo))

(defn time->second [^LocalTime foo] (.getSecond foo))

(defn date->year [^LocalDate foo] (.getYear foo))

(defn zdt->timezone [^ZonedDateTime foo] (.getZone foo))

(defn zdt->datetime [^ZonedDateTime foo] (.toLocalDateTime foo))

(defn zdt->time [^ZonedDateTime foo] (.toLocalTime foo))

(defn datetime->minute [^LocalDateTime foo] (.getMinute foo))

(defn time->nano [^LocalTime foo] (.getNano foo))

(defn time->minute [^LocalTime foo] (.getMinute foo))

(defn time->hour [^LocalTime foo] (.getHour foo))

(defn datetime->nano [^LocalDateTime foo] (.getNano foo))

(defn monthday->month [^MonthDay foo] (.getMonthValue foo))

(defn zdt->day-of-month [^ZonedDateTime foo] (.getDayOfMonth foo))

(defn yearmonth->year [^YearMonth foo] (.getYear foo))

(defn datetime->time [^LocalDateTime foo] (.toLocalTime foo))

^{:line 32, :column 9} (comment "parsers")

(defn datetime-parse [^java.lang.String foo] (LocalDateTime/parse foo))

(defn time-parse [^java.lang.String foo] (LocalTime/parse foo))

(defn zdt-parse [^java.lang.String foo] (ZonedDateTime/parse foo))

(defn date-parse [^java.lang.String foo] (LocalDate/parse foo))

(defn instant-parse [^java.lang.String foo] (Instant/parse foo))

(defn yearmonth-parse [^java.lang.String foo] (YearMonth/parse foo))

(defn monthday-parse [^java.lang.String foo] (MonthDay/parse foo))

(defn timezone-parse [^java.lang.String foo] (ZoneId/of foo))

^{:line 34, :column 9} (comment "nowers")

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

^{:line 36, :column 9} (comment "constructors")

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

