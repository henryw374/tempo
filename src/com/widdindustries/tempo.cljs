(ns
 com.widdindustries.tempo
 ""
 (:refer-clojure :exclude [min max > < >= <= >> <<])
 (:require
  [com.widdindustries.tempo.cljs-protocols :as cljs-protocols]
  [com.widdindustries.tempo.js-temporal-entities :as entities]
  [com.widdindustries.tempo.js-temporal-methods :as methods]
  [com.widdindustries.tempo.clock :as clock]
  [goog.object]))

(defn extend-all-cljs-protocols [] (cljs-protocols/extend-all))

(defn legacydate? [v] (instance? js/Date v))

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

(defn
 clock-fixed
 [instant ^String zone-str]
 (clock/clock (constantly instant) zone-str))

(defn
 clock-with-zone
 [^String timezone_id]
 (clock/clock js/Temporal.Now.instant timezone_id))

(defn
 clock-system-default-zone
 "a ticking clock having the ambient zone. "
 []
 js/Temporal.Now)

(defn
 clock-offset-millis
 [clock offset-millis]
 (clock/clock
  (fn
   []
   (.add (.instant ^js clock) (js-obj "milliseconds" offset-millis)))
  (clock/timezone_id clock)))

(defn timezone-now ([clock] (clock/timezone_id clock)))

(defn legacydate->instant [d] (.toTemporalInstant ^js d))

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__112295# p2__112296#] (greater p1__112295# p2__112296#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__112297# p2__112298#] (lesser p1__112297# p2__112298#))
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

(def nanoseconds-property "nanosecond")

(def microseconds-property "microsecond")

(def milliseconds-property "millisecond")

(def seconds-property "second")

(def minutes-property "minute")

(def hours-property "hour")

(def days-property "day")

(def months-property "month")

(def years-property "year")

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
  ^js temporal
  (js-obj property value)
  (js-obj "overflow" "reject")))

(defn
 until
 [v1 v2 property]
 (->
  (.until
   ^js v1
   ^js v2
   (js-obj "smallestUnit" property "largestUnit" property))
  (goog.object/get (str property "s"))))

(defn
 >>
 ([temporal amount temporal-property]
  (throw-if-set-months-or-years temporal temporal-property)
  (.add ^js temporal (js-obj (str temporal-property "s") amount))))

(defn
 <<
 ([temporal amount temporal-property]
  (throw-if-set-months-or-years temporal temporal-property)
  (.subtract ^js temporal (js-obj (str temporal-property "s") amount))))

(def weekday-monday "MONDAY")

(def weekday-tuesday "TUESDAY")

(def weekday-wednesday "WEDNESDAY")

(def weekday-thursday "THURSDAY")

(def weekday-friday "FRIDAY")

(def weekday-saturday "SATURDAY")

(def weekday-sunday "SUNDAY")

(def
 weekday-number->weekday
 {1 weekday-monday,
  2 weekday-tuesday,
  3 weekday-wednesday,
  4 weekday-thursday,
  5 weekday-friday,
  6 weekday-saturday,
  7 weekday-sunday})

(def
 weekday->weekday-number
 {weekday-monday 1,
  weekday-tuesday 2,
  weekday-wednesday 3,
  weekday-thursday 4,
  weekday-friday 5,
  weekday-saturday 6,
  weekday-sunday 7})

(defprotocol JavaTruncateable (-truncate [_ unit]))

(defn
 truncate
 [temporal property]
 (.round
  ^js temporal
  (js-obj "smallestUnit" property "roundingMode" "trunc")))

(defn get-field [temporal property] (goog.object/get temporal property))

(defn
 yearmonth+day-at-end-of-month
 [ym]
 (.toPlainDate ^js ym (js-obj "day" (.-daysInMonth ^js ym))))

(defn
 monthday+year
 [monthday year]
 (.toPlainDate ^js monthday (js-obj "year" year)))

(defn
 yearmonth+day
 [yearmonth day]
 (.toPlainDate ^js yearmonth (js-obj "day" day)))

(defn date+time [date time] (.toPlainDateTime ^js date time))

(defn time+date [time date] (date+time date time))

(defn
 datetime+timezone
 [datetime timezone_id]
 (.toZonedDateTime ^js datetime timezone_id))

(defn
 instant+timezone
 [instant timezone_id]
 (.toZonedDateTimeISO ^js instant timezone_id))

^{:line 31, :column 9} (comment "accessors")

(defn zdt->year [^js/Temporal.ZonedDateTime foo] (-> foo .-year))

(defn
 datetime->month
 [^js/Temporal.PlainDateTime foo]
 (-> foo (-> (.-monthCode) (subs 1 3) js/parseInt)))

(defn
 datetime->day-of-week
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-dayOfWeek))

(defn
 datetime->microsecond
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-microsecond))

(defn
 datetime->second
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-second))

(defn zdt->date [^js/Temporal.ZonedDateTime foo] (-> foo .toPlainDate))

(defn
 datetime->monthday
 [^js/Temporal.PlainDateTime foo]
 (-> foo (-> (js/Temporal.PlainMonthDay.from))))

(defn
 datetime->yearmonth
 [^js/Temporal.PlainDateTime foo]
 (-> foo (-> (js/Temporal.PlainYearMonth.from))))

(defn yearmonth->year [^js/Temporal.PlainYearMonth foo] (-> foo .-year))

(defn
 zdt->millisecond
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-millisecond))

(defn
 zdt->nanosecond
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-nanosecond))

(defn zdt->second [^js/Temporal.ZonedDateTime foo] (-> foo .-second))

(defn
 datetime->minute
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-minute))

(defn
 datetime->day-of-month
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-day))

(defn zdt->hour [^js/Temporal.ZonedDateTime foo] (-> foo .-hour))

(defn zdt->instant [^js/Temporal.ZonedDateTime foo] (-> foo .toInstant))

(defn
 zdt->month
 [^js/Temporal.ZonedDateTime foo]
 (-> foo (-> (.-monthCode) (subs 1 3) js/parseInt)))

(defn
 zdt->monthday
 [^js/Temporal.ZonedDateTime foo]
 (-> foo (-> (js/Temporal.PlainMonthDay.from))))

(defn
 monthday->month
 [^js/Temporal.PlainMonthDay foo]
 (-> foo (-> (.-monthCode) (subs 1 3) js/parseInt)))

(defn
 datetime->date
 [^js/Temporal.PlainDateTime foo]
 (-> foo .toPlainDate))

(defn
 zdt->microsecond
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-microsecond))

(defn zdt->day-of-month [^js/Temporal.ZonedDateTime foo] (-> foo .-day))

(defn
 date->monthday
 [^js/Temporal.PlainDate foo]
 (-> foo (-> (js/Temporal.PlainMonthDay.from))))

(defn
 zdt->day-of-week
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-dayOfWeek))

(defn time->second [^js/Temporal.PlainTime foo] (-> foo .-second))

(defn date->month [^js/Temporal.PlainDate foo] (-> foo .-month))

(defn
 instant->legacydate
 [^js/Temporal.Instant foo]
 (-> foo (-> (.-epochMilliseconds) (js/Date.))))

(defn date->year [^js/Temporal.PlainDate foo] (-> foo .-year))

(defn
 datetime->nanosecond
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-nanosecond))

(defn datetime->year [^js/Temporal.PlainDateTime foo] (-> foo .-year))

(defn
 instant->epochmilli
 [^js/Temporal.Instant foo]
 (-> foo (-> (.-epochMilliseconds))))

(defn datetime->hour [^js/Temporal.PlainDateTime foo] (-> foo .-hour))

(defn date->day-of-month [^js/Temporal.PlainDate foo] (-> foo .-day))

(defn
 zdt->datetime
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .toPlainDateTime))

(defn
 time->microsecond
 [^js/Temporal.PlainTime foo]
 (-> foo .-microsecond))

(defn
 time->nanosecond
 [^js/Temporal.PlainTime foo]
 (-> foo .-nanosecond))

(defn time->minute [^js/Temporal.PlainTime foo] (-> foo .-minute))

(defn time->hour [^js/Temporal.PlainTime foo] (-> foo .-hour))

(defn zdt->time [^js/Temporal.ZonedDateTime foo] (-> foo .toPlainTime))

(defn
 zdt->timezone_id
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-timeZoneId))

(defn
 monthday->day-of-month
 [^js/Temporal.PlainMonthDay foo]
 (-> foo .-day))

(defn
 date->day-of-week
 [^js/Temporal.PlainDate foo]
 (-> foo .-dayOfWeek))

(defn zdt->minute [^js/Temporal.ZonedDateTime foo] (-> foo .-minute))

(defn
 datetime->millisecond
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-millisecond))

(defn
 zdt->yearmonth
 [^js/Temporal.ZonedDateTime foo]
 (-> foo (-> (js/Temporal.PlainYearMonth.from))))

(defn
 yearmonth->month
 [^js/Temporal.PlainYearMonth foo]
 (-> foo .-month))

(defn
 time->millisecond
 [^js/Temporal.PlainTime foo]
 (-> foo .-millisecond))

(defn
 datetime->time
 [^js/Temporal.PlainDateTime foo]
 (-> foo .toPlainTime))

(defn
 date->yearmonth
 [^js/Temporal.PlainDate foo]
 (-> foo (-> (js/Temporal.PlainYearMonth.from))))

^{:line 33, :column 9} (comment "parsers")

(defn
 timezone-parse
 [^java.lang.String foo]
 (js/Temporal.TimeZone.from foo))

(defn
 instant-parse
 [^java.lang.String foo]
 (js/Temporal.Instant.from foo))

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
 yearmonth-parse
 [^java.lang.String foo]
 (js/Temporal.PlainYearMonth.from foo))

^{:line 35, :column 9} (comment "nowers")

(defn zdt-now ([^java.time.Clock clock] (clock/zdt clock)))

(defn datetime-now ([^java.time.Clock clock] (clock/datetime clock)))

(defn date-now ([^java.time.Clock clock] (clock/date clock)))

(defn monthday-now ([^java.time.Clock clock] (clock/monthday clock)))

(defn time-now ([^java.time.Clock clock] (clock/time clock)))

(defn instant-now ([^java.time.Clock clock] (clock/instant clock)))

(defn yearmonth-now ([^java.time.Clock clock] (clock/yearmonth clock)))

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
  (js/Temporal.PlainTime. hour minute second milli micro nano)))

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
  (js/Temporal.PlainDate. ^int year ^int month ^int day)))

(defn
 yearmonth-from
 [{:keys [year month]}]
 (js/Temporal.YearMonth. ^int year ^int month))

(defn
 monthday-from
 [{:keys [month day-of-month]}]
 (js/Temporal.YearMonth. ^int month ^int day-of-month))

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
   (.toZonedDateTimeISO ^js instant zone)
   (.toZonedDateTime ^js ldt zone))))

(defn
 instant-from
 [thing]
 (or
  (some->
   (get thing :epochmilli)
   (js/Temporal.Instant.fromEpochMilliseconds))
  (when-let [d (get thing :legacydate)] (.toTemporalInstant ^js d))
  (some-> (or (get thing :zdt) (zdt-from thing)) (zdt->instant))))

