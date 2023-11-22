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
  (fn* [p1__30060# p2__30061#] (greater p1__30060# p2__30061#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__30062# p2__30063#] (lesser p1__30062# p2__30063#))
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

^{:line 39, :column 9} (comment "accessors")

(defn zdt->year [^java.time.ZonedDateTime foo])

(defn datetime->second [^java.time.LocalDateTime foo])

(defn zdt->date [^java.time.ZonedDateTime foo])

(defn datetime->year [^java.time.LocalDateTime foo])

(defn datetime->month [^java.time.LocalDateTime foo])

(defn instant->epochnano [^java.time.Instant foo])

(defn datetime->hour [^java.time.LocalDateTime foo])

(defn datetime->monthday [^java.time.LocalDateTime foo])

(defn date->month [^java.time.LocalDate foo])

(defn monthday->month [^java.time.MonthDay foo])

(defn zdt->hour [^java.time.ZonedDateTime foo])

(defn zdt->instant [^java.time.ZonedDateTime foo])

(defn zdt->nano [^java.time.ZonedDateTime foo])

(defn yearmonth->month [^java.time.YearMonth foo])

(defn zdt->timezone [^java.time.ZonedDateTime foo])

(defn zdt->minute [^java.time.ZonedDateTime foo])

(defn datetime->date [^java.time.LocalDateTime foo])

(defn date->day-of-month [^java.time.LocalDate foo])

(defn date->year [^java.time.LocalDate foo])

(defn zdt->second [^java.time.ZonedDateTime foo])

(defn monthday->day-of-month [^java.time.MonthDay foo])

(defn instant->epochmilli [^java.time.Instant foo])

(defn time->second [^java.time.LocalTime foo])

(defn datetime->day-of-month [^java.time.LocalDateTime foo])

(defn date->monthday [^java.time.LocalDate foo])

(defn yearmonth->year [^java.time.YearMonth foo])

(defn zdt->datetime [^java.time.ZonedDateTime foo])

(defn datetime->day-of-week [^java.time.LocalDateTime foo])

(defn datetime->yearmonth [^java.time.LocalDateTime foo])

(defn date->day-of-week [^java.time.LocalDate foo])

(defn date->yearmonth [^java.time.LocalDate foo])

(defn zdt->time [^java.time.ZonedDateTime foo])

(defn zdt->yearmonth [^java.time.ZonedDateTime foo])

(defn zdt->day-of-month [^java.time.ZonedDateTime foo])

(defn zdt->month [^java.time.ZonedDateTime foo])

(defn datetime->minute [^java.time.LocalDateTime foo])

(defn zdt->monthday [^java.time.ZonedDateTime foo])

(defn time->nano [^java.time.LocalTime foo])

(defn zdt->day-of-week [^java.time.ZonedDateTime foo])

(defn time->minute [^java.time.LocalTime foo])

(defn time->hour [^java.time.LocalTime foo])

(defn datetime->nano [^java.time.LocalDateTime foo])

(defn datetime->time [^java.time.LocalDateTime foo])

^{:line 41, :column 9} (comment "parsers")

(defn
 zdt-parse
 [^java.lang.String foo]
 (js/Temporal.ZonedDateTime.from foo))

(defn
 datetime-parse
 [^java.lang.String foo]
 (js/Temporal.PlainDateTime.from foo))

(defn
 date-parse
 [^java.lang.String foo]
 (js/Temporal.PlainDate.from foo))

(defn
 yearmonth-parse
 [^java.lang.String foo]
 (js/Temporal.PlainYearMonth.from foo))

(defn
 time-parse
 [^java.lang.String foo]
 (js/Temporal.PlainTime.from foo))

(defn
 instant-parse
 [^java.lang.String foo]
 (js/Temporal.Instant.from foo))

(defn
 monthday-parse
 [^java.lang.String foo]
 (js/Temporal.PlainMonthDay.from foo))

(defn
 timezone-parse
 [^java.lang.String foo]
 (js/Temporal.TimeZone.from foo))

^{:line 43, :column 9} (comment "nowers")

(defn
 zdt-now
 ([] (clock/zdt))
 ([^java.time.Clock clock] (clock/zdt clock)))

(defn
 datetime-now
 ([] (clock/datetime))
 ([^java.time.Clock clock] (clock/datetime clock)))

(defn
 date-now
 ([] (clock/date))
 ([^java.time.Clock clock] (clock/date clock)))

(defn
 yearmonth-now
 ([] (clock/yearmonth))
 ([^java.time.Clock clock] (clock/yearmonth clock)))

(defn
 time-now
 ([] (clock/time))
 ([^java.time.Clock clock] (clock/time clock)))

(defn
 instant-now
 ([] (clock/instant))
 ([^java.time.Clock clock] (clock/instant clock)))

(defn
 monthday-now
 ([] (clock/monthday))
 ([^java.time.Clock clock] (clock/monthday clock)))

^{:line 45, :column 9} (comment "constructors")

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

