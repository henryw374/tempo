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

(defn monthday? [v] (instance? entities/monthday v))

(defn yearmonth? [v] (instance? entities/yearmonth v))

(defn timezone? [v] (instance? entities/timezone v))

(defn zdt? [v] (instance? entities/zdt v))

(defn zone-system-default [] (clock/time-zone))

(defn clock-fixed [instant zone] (clock/fixed-clock instant zone))

(defn
 clock-system-default-zone
 "a ticking clock having the ambient zone"
 []
 js/Temporal.Now)

(defn clock-offset [offset-or-zone])

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__38372# p2__38373#] (greater p1__38372# p2__38373#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__38374# p2__38375#] (lesser p1__38374# p2__38375#))
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

(def
 nanos-property
 (reify
  Property
  (field [_] "nanosecond")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "nanoseconds")
    (unit-field [_] "nanosecond")
    (unit-accessor [_ ^js x] (.-nanos x))))))

(def
 micros-property
 (reify
  Property
  (field [_] "microsecond")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "microseconds")
    (unit-field [_] "microsecond")
    (unit-accessor [_ ^js x] (.-micros x))))))

(def
 millis-property
 (reify
  Property
  (field [_] "millisecond")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "milliseconds")
    (unit-field [_] "millisecond")
    (unit-accessor [_ ^js x] (.-millis x))))))

(def
 seconds-property
 (reify
  Property
  (field [_] "second")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "seconds")
    (unit-field [_] "second")
    (unit-accessor [_ ^js x] (.-seconds x))))))

(def
 minutes-property
 (reify
  Property
  (field [_] "minute")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "minutes")
    (unit-field [_] "minute")
    (unit-accessor [_ ^js x] (.-minutes x))))))

(def
 hours-property
 (reify
  Property
  (field [_] "hour")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "hours")
    (unit-field [_] "hour")
    (unit-accessor [_ ^js x] (.-hours x))))))

(def
 days-property
 (reify
  Property
  (field [_] "day")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "days")
    (unit-field [_] "day")
    (unit-accessor [_ ^js x] (.-days x))))))

(def
 months-property
 (reify
  Property
  (field [_] "month")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "months")
    (unit-field [_] "month")
    (unit-accessor [_ ^js x] (.-months x))))))

(def
 years-property
 (reify
  Property
  (field [_] "year")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "years")
    (unit-field [_] "year")
    (unit-accessor [_ ^js x] (.-years x))))))

(defn
 with
 [temporal value property]
 (.with ^js temporal (js-obj (field property) value)))

(defn
 until
 [v1 v2 property]
 (->
  (.until
   ^js v1
   ^js v2
   #js {:smallestUnit (unit-field (unit property)), :largestUnit (unit-field (unit property))})
  (unit-accessor (unit property))))

(defn
 >>
 ([temporal temporal-amount] (.add ^js temporal temporal-amount))
 ([temporal amount temporal-property]
  (.add
   ^js temporal
   (js-obj (unit-amount (unit temporal-property)) amount))))

(defn
 <<
 ([temporal temporal-amount] (.subtract ^js temporal temporal-amount))
 ([temporal amount temporal-property]
  (.subtract
   ^js temporal
   (js-obj (unit-amount (unit temporal-property)) amount))))

(defprotocol WeekDay (weekday-number [_]) (english-name [_]))

(def
 weekday-monday
 (reify WeekDay (weekday-number [_] 1) (english-name [_] "MONDAY")))

(def
 weekday-tuesday
 (reify WeekDay (weekday-number [_] 2) (english-name [_] "TUESDAY")))

(def
 weekday-wednesday
 (reify WeekDay (weekday-number [_] 3) (english-name [_] "WEDNESDAY")))

(def
 weekday-thursday
 (reify WeekDay (weekday-number [_] 4) (english-name [_] "THURSDAY")))

(def
 weekday-friday
 (reify WeekDay (weekday-number [_] 5) (english-name [_] "FRIDAY")))

(def
 weekday-saturday
 (reify WeekDay (weekday-number [_] 6) (english-name [_] "SATURDAY")))

(def
 weekday-sunday
 (reify WeekDay (weekday-number [_] 7) (english-name [_] "SUNDAY")))

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

(defn date->month [^js/Temporal.PlainDate foo] (-> foo .-month))

(defn
 yearmonth->month
 [^js/Temporal.PlainYearMonth foo]
 (-> foo .-month))

(defn zdt->month [^js/Temporal.ZonedDateTime foo] (-> foo .-month))

(defn
 datetime->second
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-second))

(defn zdt->date [^js/Temporal.ZonedDateTime foo] (-> foo .toPlainDate))

(defn datetime->year [^js/Temporal.PlainDateTime foo] (-> foo .-year))

(defn datetime->month [^js/Temporal.PlainDateTime foo] (-> foo .-month))

(defn
 monthday->day-of-month
 [^js/Temporal.PlainMonthDay foo]
 (-> foo .-day))

(defn zdt->year [^js/Temporal.ZonedDateTime foo] (-> foo .-year))

(defn datetime->hour [^js/Temporal.PlainDateTime foo] (-> foo .-hour))

(defn date->day-of-month [^js/Temporal.PlainDate foo] (-> foo .-day))

(defn zdt->hour [^js/Temporal.ZonedDateTime foo] (-> foo .-hour))

(defn zdt->instant [^js/Temporal.ZonedDateTime foo] (-> foo .toInstant))

(defn zdt->nano [^js/Temporal.ZonedDateTime foo] (-> foo .-nano))

(defn
 zdt->day-of-week
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-dayOfWeek))

(defn zdt->minute [^js/Temporal.ZonedDateTime foo] (-> foo .-minute))

(defn
 datetime->day-of-month
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-day))

(defn
 datetime->date
 [^js/Temporal.PlainDateTime foo]
 (-> foo .toPlainDate))

(defn date->year [^js/Temporal.PlainDate foo] (-> foo .-year))

(defn zdt->second [^js/Temporal.ZonedDateTime foo] (-> foo .-second))

(defn zdt->day-of-month [^js/Temporal.ZonedDateTime foo] (-> foo .-day))

(defn
 instant->epochmilli
 [^js/Temporal.Instant foo]
 (-> foo .-epochmilli))

(defn time->second [^js/Temporal.PlainTime foo] (-> foo .-second))

(defn
 date->day-of-week
 [^js/Temporal.PlainDate foo]
 (-> foo .-dayOfWeek))

(defn
 zdt->datetime
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .toPlainDateTime))

(defn
 zdt->timezone
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-timeZoneId))

(defn zdt->time [^js/Temporal.ZonedDateTime foo] (-> foo .toPlainTime))

(defn
 datetime->minute
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-minute))

(defn
 instant->legacydate
 [^js/Temporal.Instant foo]
 (-> foo .-legacydate))

(defn
 datetime->day-of-week
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-dayOfWeek))

(defn time->nano [^js/Temporal.PlainTime foo] (-> foo .-nano))

(defn time->minute [^js/Temporal.PlainTime foo] (-> foo .-minute))

(defn time->hour [^js/Temporal.PlainTime foo] (-> foo .-hour))

(defn monthday->month [^js/Temporal.PlainMonthDay foo] (-> foo .-month))

(defn datetime->nano [^js/Temporal.PlainDateTime foo] (-> foo .-nano))

(defn yearmonth->year [^js/Temporal.PlainYearMonth foo] (-> foo .-year))

(defn
 datetime->time
 [^js/Temporal.PlainDateTime foo]
 (-> foo .toPlainTime))

^{:line 33, :column 9} (comment "parsers")

(defn
 date-parse
 [^java.lang.String foo]
 (js/Temporal.PlainDate.from foo))

(defn
 yearmonth-parse
 [^java.lang.String foo]
 (js/Temporal.PlainYearMonth.from foo))

(defn
 zdt-parse
 [^java.lang.String foo]
 (js/Temporal.ZonedDateTime.from foo))

(defn
 datetime-parse
 [^java.lang.String foo]
 (js/Temporal.PlainDateTime.from foo))

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
 timezone-parse
 [^java.lang.String foo]
 (js/Temporal.TimeZone.from foo))

^{:line 35, :column 9} (comment "nowers")

(defn
 date-now
 ([] (clock/date))
 ([^java.time.Clock clock] (clock/date clock)))

(defn
 yearmonth-now
 ([] (clock/yearmonth))
 ([^java.time.Clock clock] (clock/yearmonth clock)))

(defn
 zdt-now
 ([] (clock/zdt))
 ([^java.time.Clock clock] (clock/zdt clock)))

(defn
 datetime-now
 ([] (clock/datetime))
 ([^java.time.Clock clock] (clock/datetime clock)))

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

(defn
 instant-from
 [thing]
 (or
  (some->
   (get thing :epochmilli)
   (js/Temporal.Instant.fromEpochMilliseconds))
  (when-let [d (get thing :legacydate)] (.toTemporalInstant ^js d))
  (some-> (or (get thing :zdt) (zdt-from thing)) (zdt->instant))))

