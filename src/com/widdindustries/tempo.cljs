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

(defn
 clock-system-default-zone
 "a ticking clock having the ambient zone"
 []
 js/Temporal.Now)

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__39058# p2__39059#] (greater p1__39058# p2__39059#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__39060# p2__39061#] (lesser p1__39060# p2__39061#))
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

(defn zdt->month [^js/Temporal.ZonedDateTime foo] (.-month foo))

(defn
 zdt->day-of-month
 [^js/Temporal.ZonedDateTime foo]
 (.-dayOfMonth foo))

(defn datetime->second [^js/Temporal.PlainDateTime foo] (.-second foo))

(defn zdt->date [^js/Temporal.ZonedDateTime foo] (.toPlainDate foo))

(defn datetime->year [^js/Temporal.PlainDateTime foo] (.-year foo))

(defn date->month [^js/Temporal.PlainDate foo] (.-month foo))

(defn datetime->month [^js/Temporal.PlainDateTime foo] (.-month foo))

(defn zdt->year [^js/Temporal.ZonedDateTime foo] (.-year foo))

(defn datetime->hour [^js/Temporal.PlainDateTime foo] (.-hour foo))

(defn yearmonth->month [^js/Temporal.PlainYearMonth foo] (.-month foo))

(defn
 monthday->day-of-month
 [^js/Temporal.PlainMonthDay foo]
 (.-dayOfMonth foo))

(defn
 datetime->day-of-month
 [^js/Temporal.PlainDateTime foo]
 (.-dayOfMonth foo))

(defn zdt->hour [^js/Temporal.ZonedDateTime foo] (.-hour foo))

(defn zdt->instant [^js/Temporal.ZonedDateTime foo] (.toInstant foo))

(defn zdt->nano [^js/Temporal.ZonedDateTime foo] (.-nano foo))

(defn yearmonth->year [^js/Temporal.PlainYearMonth foo] (.-year foo))

(defn zdt->minute [^js/Temporal.ZonedDateTime foo] (.-minute foo))

(defn
 datetime->date
 [^js/Temporal.PlainDateTime foo]
 (.toPlainDate foo))

(defn
 date->day-of-month
 [^js/Temporal.PlainDate foo]
 (.-dayOfMonth foo))

(defn zdt->second [^js/Temporal.ZonedDateTime foo] (.-second foo))

(defn instant->epochmilli [^js/Temporal.Instant foo] (.-epochmilli foo))

(defn time->second [^js/Temporal.PlainTime foo] (.-second foo))

(defn date->year [^js/Temporal.PlainDate foo] (.-year foo))

(defn
 zdt->datetime
 [^js/Temporal.ZonedDateTime foo]
 (.toPlainDateTime foo))

(defn zdt->time [^js/Temporal.ZonedDateTime foo] (.toPlainTime foo))

(defn monthday->month [^js/Temporal.PlainMonthDay foo] (.-month foo))

(defn datetime->minute [^js/Temporal.PlainDateTime foo] (.-minute foo))

(defn time->nano [^js/Temporal.PlainTime foo] (.-nano foo))

(defn zdt->timezone [^js/Temporal.ZonedDateTime foo] (.timeZoneId foo))

(defn time->minute [^js/Temporal.PlainTime foo] (.-minute foo))

(defn time->hour [^js/Temporal.PlainTime foo] (.-hour foo))

(defn datetime->nano [^js/Temporal.PlainDateTime foo] (.-nano foo))

(defn
 datetime->time
 [^js/Temporal.PlainDateTime foo]
 (.toPlainTime foo))

^{:line 32, :column 9} (comment "parsers")

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
 monthday-parse
 [^java.lang.String foo]
 (js/Temporal.PlainMonthDay.from foo))

(defn
 time-parse
 [^java.lang.String foo]
 (js/Temporal.PlainTime.from foo))

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

^{:line 34, :column 9} (comment "nowers")

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
 monthday-now
 ([] (clock/monthday))
 ([^java.time.Clock clock] (clock/monthday clock)))

(defn
 time-now
 ([] (clock/time))
 ([^java.time.Clock clock] (clock/time clock)))

(defn
 instant-now
 ([] (clock/instant))
 ([^java.time.Clock clock] (clock/instant clock)))

(defn
 yearmonth-now
 ([] (clock/yearmonth))
 ([^java.time.Clock clock] (clock/yearmonth clock)))

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

