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

(defn legacydate? [v] (instance? js/Date v))

(defn clock? [v] nil)

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

(defn timezone-system-default [] (clock/timezone))

(defn
 timezone-now
 ([] (timezone-system-default))
 ([clock] (clock/timezone clock)))

(defn
 clock-fixed
 [instant ^String zone-str]
 (clock/fixed-clock instant zone-str))

(defn
 clock-system-default-zone
 "a ticking clock having the ambient zone. "
 []
 js/Temporal.Now)

(defn
 clock-offset-millis
 [clock offset-millis]
 (clock/offset-clock-millis clock offset-millis))

(defn legacydate->instant [d] (.toTemporalInstant d))

(defn greater [x y] (if (neg? (compare x y)) y x))

(defn
 max
 "Find the latest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__150644# p2__150645#] (greater p1__150644# p2__150645#))
  arg
  args))

(defn lesser [x y] (if (neg? (compare x y)) x y))

(defn
 min
 "Find the earliest of the given arguments. Callers should ensure that no\r\n  argument is nil."
 [arg & args]
 (assert (every? some? (cons arg args)))
 (reduce
  (fn* [p1__150646# p2__150647#] (lesser p1__150646# p2__150647#))
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

(defn
 -millisecond
 [f]
 (-> (getFractional f) (Duration/ofNanos) (.toMillisPart)))

(defn -microsecond [f] (-> (getFractional f) (/ 1000) long (mod 1000)))

(defn -nanosecond [f] (-> (getFractional f) (mod 1000)))

(def ^ValueRange sub-second-range (ValueRange/of 0 999))

(def
 nanoseconds-property
 (reify
  object
  (toString [_] "nanoseconds-property")
  Property
  (field [_] "nanosecond")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "nanoseconds")
    (unit-field [_] "nanosecond")
    (unit-accessor [_ ^js x] (.-nanos x))))))

(defmethod
 print-method
 (type nanoseconds-property)
 [_ ^java.io.Writer w]
 (print-simple "nanoseconds-property" w))

(def
 microseconds-property
 (reify
  object
  (toString [_] "microseconds-property")
  Property
  (field [_] "microsecond")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "microseconds")
    (unit-field [_] "microsecond")
    (unit-accessor [_ ^js x] (.-micros x))))))

(defmethod
 print-method
 (type microseconds-property)
 [_ ^java.io.Writer w]
 (print-simple "microseconds-property" w))

(def
 milliseconds-property
 (reify
  object
  (toString [_] "milliseconds-property")
  Property
  (field [_] "millisecond")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "milliseconds")
    (unit-field [_] "millisecond")
    (unit-accessor [_ ^js x] (.-millis x))))))

(defmethod
 print-method
 (type milliseconds-property)
 [_ ^java.io.Writer w]
 (print-simple "milliseconds-property" w))

(def
 seconds-property
 (reify
  object
  (toString [_] "seconds-property")
  Property
  (field [_] "second")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "seconds")
    (unit-field [_] "second")
    (unit-accessor [_ ^js x] (.-seconds x))))))

(defmethod
 print-method
 (type seconds-property)
 [_ ^java.io.Writer w]
 (print-simple "seconds-property" w))

(def
 minutes-property
 (reify
  object
  (toString [_] "minutes-property")
  Property
  (field [_] "minute")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "minutes")
    (unit-field [_] "minute")
    (unit-accessor [_ ^js x] (.-minutes x))))))

(defmethod
 print-method
 (type minutes-property)
 [_ ^java.io.Writer w]
 (print-simple "minutes-property" w))

(def
 hours-property
 (reify
  object
  (toString [_] "hours-property")
  Property
  (field [_] "hour")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "hours")
    (unit-field [_] "hour")
    (unit-accessor [_ ^js x] (.-hours x))))))

(defmethod
 print-method
 (type hours-property)
 [_ ^java.io.Writer w]
 (print-simple "hours-property" w))

(def
 days-property
 (reify
  object
  (toString [_] "days-property")
  Property
  (field [_] "day")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "days")
    (unit-field [_] "day")
    (unit-accessor [_ ^js x] (.-days x))))))

(defmethod
 print-method
 (type days-property)
 [_ ^java.io.Writer w]
 (print-simple "days-property" w))

(def
 months-property
 (reify
  object
  (toString [_] "months-property")
  Property
  (field [_] "month")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "months")
    (unit-field [_] "month")
    (unit-accessor [_ ^js x] (.-months x))))))

(defmethod
 print-method
 (type months-property)
 [_ ^java.io.Writer w]
 (print-simple "months-property" w))

(def
 years-property
 (reify
  object
  (toString [_] "years-property")
  Property
  (field [_] "year")
  (unit
   [_]
   (reify
    Unit
    (unit-amount [_] "years")
    (unit-field [_] "year")
    (unit-accessor [_ ^js x] (.-years x))))))

(defmethod
 print-method
 (type years-property)
 [_ ^java.io.Writer w]
 (print-simple "years-property" w))

(def ^{:dynamic true} *block-non-commutative-operations* true)

(defn
 assert-set-months-or-years
 [temporal temporal-property]
 (when
  *block-non-commutative-operations*
  (assert
   (not
    (and
     (contains? #{months-property years-property} temporal)
     (not (or (monthday? temporal) (yearmonth? temporal)))))
   "shifting by years or months yields odd results depending on input. intead shift a year-month, then set non-yearmonth parts")))

(defn
 with
 [temporal value property]
 (assert-set-months-or-years temporal property)
 (.with
  ^js temporal
  (js-obj (field property) value)
  #js {"overflow" "reject"}))

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
 ([temporal amount temporal-property]
  (assert-set-months-or-years temporal temporal-property)
  (.add
   ^js temporal
   (js-obj (unit-amount (unit temporal-property)) amount))))

(defn
 <<
 ([temporal amount temporal-property]
  (assert-set-months-or-years temporal temporal-property)
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

(defn
 truncate
 [temporal property]
 (.roundTo temporal (unit-field (unit property))))

(defn get-field [temporal property] (unit-accessor property temporal))

^{:line 31, :column 9} (comment "accessors")

(defn zdt->year [^js/Temporal.ZonedDateTime foo] (-> foo .-year))

(defn
 datetime->day-of-week
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-dayOfWeek))

(defn
 zdt->millisecond
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-millisecond))

(defn
 datetime->second
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-second))

(defn zdt->date [^js/Temporal.ZonedDateTime foo] (-> foo .toPlainDate))

(defn
 zdt->microsecond
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-microsecond))

(defn date->month [^js/Temporal.PlainDate foo] (-> foo .-month))

(defn
 datetime->millisecond
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-millisecond))

(defn date->year [^js/Temporal.PlainDate foo] (-> foo .-year))

(defn zdt->month [^js/Temporal.ZonedDateTime foo] (-> foo .-month))

(defn zdt->day-of-month [^js/Temporal.ZonedDateTime foo] (-> foo .-day))

(defn
 time->nanosecond
 [^js/Temporal.PlainTime foo]
 (-> foo .-nanosecond))

(defn
 datetime->microsecond
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-microsecond))

(defn zdt->second [^js/Temporal.ZonedDateTime foo] (-> foo .-second))

(defn
 datetime->minute
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-minute))

(defn
 zdt->nanosecond
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-nanosecond))

(defn zdt->hour [^js/Temporal.ZonedDateTime foo] (-> foo .-hour))

(defn
 time->millisecond
 [^js/Temporal.PlainTime foo]
 (-> foo .-millisecond))

(defn zdt->instant [^js/Temporal.ZonedDateTime foo] (-> foo .toInstant))

(defn datetime->month [^js/Temporal.PlainDateTime foo] (-> foo .-month))

(defn
 datetime->day-of-month
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-day))

(defn
 time->microsecond
 [^js/Temporal.PlainTime foo]
 (-> foo .-microsecond))

(defn
 datetime->date
 [^js/Temporal.PlainDateTime foo]
 (-> foo .toPlainDate))

(defn
 zdt->day-of-week
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-dayOfWeek))

(defn time->second [^js/Temporal.PlainTime foo] (-> foo .-second))

(defn yearmonth->year [^js/Temporal.PlainYearMonth foo] (-> foo .-year))

(defn datetime->year [^js/Temporal.PlainDateTime foo] (-> foo .-year))

(defn
 monthday->day-of-month
 [^js/Temporal.PlainMonthDay foo]
 (-> foo .-day))

(defn
 yearmonth->month
 [^js/Temporal.PlainYearMonth foo]
 (-> foo .-month))

(defn
 datetime->nanosecond
 [^js/Temporal.PlainDateTime foo]
 (-> foo .-nanosecond))

(defn datetime->hour [^js/Temporal.PlainDateTime foo] (-> foo .-hour))

(defn date->day-of-month [^js/Temporal.PlainDate foo] (-> foo .-day))

(defn
 zdt->datetime
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .toPlainDateTime))

(defn time->minute [^js/Temporal.PlainTime foo] (-> foo .-minute))

(defn time->hour [^js/Temporal.PlainTime foo] (-> foo .-hour))

(defn zdt->time [^js/Temporal.ZonedDateTime foo] (-> foo .toPlainTime))

(defn
 zdt->timezone_id
 [^js/Temporal.ZonedDateTime foo]
 (-> foo .-timeZoneId))

(defn monthday->month [^js/Temporal.PlainMonthDay foo] (-> foo .-month))

(defn
 date->day-of-week
 [^js/Temporal.PlainDate foo]
 (-> foo .-dayOfWeek))

(defn zdt->minute [^js/Temporal.ZonedDateTime foo] (-> foo .-minute))

(defn
 instant->legacydate
 [^js/Temporal.Instant foo]
 (-> foo (-> (.-epochMilliseconds) (Date.))))

(defn
 instant->epochmilli
 [^js/Temporal.Instant foo]
 (-> foo .-epochmilli))

(defn
 datetime->time
 [^js/Temporal.PlainDateTime foo]
 (-> foo .toPlainTime))

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
 time-parse
 [^java.lang.String foo]
 (js/Temporal.PlainTime.from foo))

(defn
 monthday-parse
 [^java.lang.String foo]
 (js/Temporal.PlainMonthDay.from foo))

(defn
 yearmonth-parse
 [^java.lang.String foo]
 (js/Temporal.PlainYearMonth.from foo))

^{:line 35, :column 9} (comment "nowers")

(defn zdt-now ([^java.time.Clock clock] (clock/zdt clock)))

(defn datetime-now ([^java.time.Clock clock] (clock/datetime clock)))

(defn date-now ([^java.time.Clock clock] (clock/date clock)))

(defn time-now ([^java.time.Clock clock] (clock/time clock)))

(defn instant-now ([^java.time.Clock clock] (clock/instant clock)))

(defn monthday-now ([^java.time.Clock clock] (clock/monthday clock)))

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
  [ldt
   (or (get thing :datetime) (datetime-from thing))
   zone
   (get thing :timezone_id)]
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

