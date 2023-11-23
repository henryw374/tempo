(ns
 com.widdindustries.tempo
 ""
 (:refer-clojure :exclude [min max > < >= <= >> <<])
 (:require
  [com.widdindustries.tempo.cljs-protocols :as cljs-protocols]
  [com.widdindustries.tempo.js-temporal-entities :as entities]
  [com.widdindustries.tempo.js-temporal-methods :as methods]
  [com.widdindustries.tempo.clock :as clock]))

(defn extend-all-cljs-protocols [] (cljs-protocols/extend-all))

(defn period? [v] (instance? entities/duration v))

(defn duration? [v] (instance? entities/duration v))

(defn instant? [v] (instance? entities/instant v))

(defn date? [v] (instance? entities/date v))

(defn datetime? [v] (instance? entities/datetime v))

(defn time? [v] (instance? entities/time v))

(defn monthday? [v] (instance? entities/month-day v))

(defn yearmonth? [v] (instance? entities/year-month v))

(defn timezone? [v] (instance? entities/time-zone v))

(defn zdt? [v] (instance? entities/zdt v))

(defn >> [temporal temporal-amount] (.add ^js temporal temporal-amount))

(defn
 <<
 [temporal temporal-amount]
 (.subtract ^js temporal temporal-amount))

(defn zone-system-default [] (clock/time-zone))

(defn duration->negated [d] (.negated ^js d))

(defn duration-negative? [d] (neg? (.-sign ^js d)))

(defn clock-fixed [instant zone] (clock/fixed-clock instant zone))

(defn clock-system-default-zone [] js/Temporal.Now)

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__34825# p2__34826#] (greater p1__34825# p2__34826#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__34827# p2__34828#] (lesser p1__34827# p2__34828#))
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

^{:line 31, :column 9} (comment "accessors")

(defn datetime->second [^LocalDateTime foo])

(defn zdt->date [^ZonedDateTime foo])

(defn datetime->year [^LocalDateTime foo])

(defn datetime->month [^LocalDateTime foo])

(defn date->month [^LocalDate foo])

(defn instant->epochnano [^Instant foo])

(defn zdt->yearmonth [^ZonedDateTime foo])

(defn zdt->year [^ZonedDateTime foo])

(defn zdt->day-of-month [^ZonedDateTime foo])

(defn datetime->hour [^LocalDateTime foo])

(defn yearmonth->year [^YearMonth foo])

(defn zdt->hour [^ZonedDateTime foo])

(defn zdt->instant [^ZonedDateTime foo])

(defn zdt->nano [^ZonedDateTime foo])

(defn datetime->day-of-month [^LocalDateTime foo])

(defn zdt->timezone [^ZonedDateTime foo])

(defn date->day-of-month [^LocalDate foo])

(defn zdt->minute [^ZonedDateTime foo])

(defn datetime->monthday [^LocalDateTime foo])

(defn datetime->date [^LocalDateTime foo])

(defn zdt->month [^ZonedDateTime foo])

(defn zdt->second [^ZonedDateTime foo])

(defn date->yearmonth [^LocalDate foo])

(defn datetime->yearmonth [^LocalDateTime foo])

(defn monthday->day-of-month [^MonthDay foo])

(defn yearmonth->month [^YearMonth foo])

(defn monthday->month [^MonthDay foo])

(defn instant->epochmilli [^Instant foo])

(defn date->year [^LocalDate foo])

(defn time->second [^LocalTime foo])

(defn zdt->datetime [^ZonedDateTime foo])

(defn date->monthday [^LocalDate foo])

(defn datetime->day-of-week [^LocalDateTime foo])

(defn date->day-of-week [^LocalDate foo])

(defn zdt->time [^ZonedDateTime foo])

(defn zdt->monthday [^ZonedDateTime foo])

(defn datetime->minute [^LocalDateTime foo])

(defn time->nano [^LocalTime foo])

(defn zdt->day-of-week [^ZonedDateTime foo])

(defn time->minute [^LocalTime foo])

(defn time->hour [^LocalTime foo])

(defn datetime->nano [^LocalDateTime foo])

(defn datetime->time [^LocalDateTime foo])

^{:line 33, :column 9} (comment "parsers")

(defn
 datetime-parse
 [^java.lang.String foo]
 (js/Temporal.PlainDateTime.from foo))

(defn
 time-parse
 [^java.lang.String foo]
 (js/Temporal.PlainTime.from foo))

(defn
 zdt-parse
 [^java.lang.String foo]
 (js/Temporal.ZonedDateTime.from foo))

(defn
 date-parse
 [^java.lang.String foo]
 (js/Temporal.PlainDate.from foo))

(defn
 monthday-parse
 [^java.lang.String foo]
 (js/Temporal.PlainMonthDay.from foo))

(defn
 instant-parse
 [^java.lang.String foo]
 (js/Temporal.Instant.from foo))

(defn
 yearmonth-parse
 [^java.lang.String foo]
 (js/Temporal.PlainYearMonth.from foo))

(defn
 timezone-parse
 [^java.lang.String foo]
 (js/Temporal.TimeZone.from foo))

^{:line 35, :column 9} (comment "nowers")

(defn
 datetime-now
 ([] (clock/datetime))
 ([^java.time.Clock clock] (clock/datetime clock)))

(defn
 time-now
 ([] (clock/time))
 ([^java.time.Clock clock] (clock/time clock)))

(defn
 zdt-now
 ([] (clock/zdt))
 ([^java.time.Clock clock] (clock/zdt clock)))

(defn
 date-now
 ([] (clock/date))
 ([^java.time.Clock clock] (clock/date clock)))

(defn
 monthday-now
 ([] (clock/monthday))
 ([^java.time.Clock clock] (clock/monthday clock)))

(defn
 instant-now
 ([] (clock/instant))
 ([^java.time.Clock clock] (clock/instant clock)))

(defn
 yearmonth-now
 ([] (clock/yearmonth))
 ([^java.time.Clock clock] (clock/yearmonth clock)))

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
  (js/Temporal.PlainTime.
   hour
   minute
   second
   (-> (Math/floor (/ nano 1000)) (* 1000))
   (mod nano 1000))))

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
  (js/Temporal.PlainDate. ^int year ^int month ^int day)))

(defn
 datetime-from
 [thing]
 (let
  [date
   (or (get thing :date) (date-from thing))
   time
   (or (get thing :time) (time-from thing))]
  (.toPlainDateTime ^js date time)))

(defn
 zdt-from
 [thing]
 (let
  [ldt
   (or (get thing :datetime) (datetime-from thing))
   zone
   (get thing :timezone)]
  (.toZonedDateTime ^js ldt zone)))

