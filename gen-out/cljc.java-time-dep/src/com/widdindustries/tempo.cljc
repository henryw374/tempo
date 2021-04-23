(ns
 com.widdindustries.tempo
 ""
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
  [cljc.java-time.duration]))

(set! *warn-on-reflection* true)

(defn
 datetime->second
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-second foo))

(defn
 datetime->year
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-year foo))

(defn
 zdt->date
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/to-local-date foo))

(defn
 datetime->year
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-year foo))

(defn
 datetime->month
 [^java.time.LocalDateTime foo]
 (.getMonthValue foo))

(defn
 zdt->year
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-year foo))

(defn
 yearmonth->year
 [^java.time.YearMonth foo]
 (cljc.java-time.year-month/get-year foo))

(defn
 datetime->hour
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-hour foo))

(defn date->month [^java.time.LocalDate foo] (.getMonthValue foo))

(defn
 zdt->year
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-year foo))

(defn
 zdt->day-of-month
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-day-of-month foo))

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
 datetime->month
 [^java.time.LocalDateTime foo]
 (.getMonthValue foo))

(defn zdt->timezone [^java.time.ZonedDateTime foo] (.getZone foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonthValue foo))

(defn
 zdt->minute
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-minute foo))

(defn
 datetime->date
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/to-local-date foo))

(defn
 date->day-of-month
 [^java.time.LocalDate foo]
 (cljc.java-time.local-date/get-day-of-month foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonthValue foo))

(defn yearmonth->month [^java.time.YearMonth foo] (.getMonthValue foo))

(defn date->month [^java.time.LocalDate foo] (.getMonthValue foo))

(defn
 datetime->day-of-month
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-day-of-month foo))

(defn
 zdt->second
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-second foo))

(defn instant->epochmilli [^java.time.Instant foo] (.toEpochMilli foo))

(defn
 date->day-of-month
 [^java.time.LocalDate foo]
 (cljc.java-time.local-date/get-day-of-month foo))

(defn
 time->second
 [^java.time.LocalTime foo]
 (cljc.java-time.local-time/get-second foo))

(defn
 datetime->day-of-month
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/get-day-of-month foo))

(defn
 date->year
 [^java.time.LocalDate foo]
 (cljc.java-time.local-date/get-year foo))

(defn
 date->year
 [^java.time.LocalDate foo]
 (cljc.java-time.local-date/get-year foo))

(defn
 zdt->datetime
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/to-local-date-time foo))

(defn
 datetime->month
 [^java.time.LocalDateTime foo]
 (.getMonthValue foo))

(defn
 zdt->time
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/to-local-time foo))

(defn monthday->month [^java.time.MonthDay foo] (.getMonthValue foo))

(defn
 zdt->day-of-month
 [^java.time.ZonedDateTime foo]
 (cljc.java-time.zoned-date-time/get-day-of-month foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonthValue foo))

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
 monthday->day-of-month
 [^java.time.MonthDay foo]
 (cljc.java-time.month-day/get-day-of-month foo))

(defn date->month [^java.time.LocalDate foo] (.getMonthValue foo))

(defn
 datetime->time
 [^java.time.LocalDateTime foo]
 (cljc.java-time.local-date-time/to-local-time foo))

(defn
 date-parse
 [^java.lang.String foo]
 (cljc.java-time.local-date/parse foo))

(defn
 monthday-parse
 [^java.lang.String foo]
 (cljc.java-time.month-day/parse foo))

(defn
 datetime-parse
 [^java.lang.String foo]
 (cljc.java-time.local-date-time/parse foo))

(defn
 time-parse
 [^java.lang.String foo]
 (cljc.java-time.local-time/parse foo))

(defn
 yearmonth-parse
 [^java.lang.String foo]
 (cljc.java-time.year-month/parse foo))

(defn
 zdt-parse
 [^java.lang.String foo]
 (cljc.java-time.zoned-date-time/parse foo))

(defn
 instant-parse
 [^java.lang.String foo]
 (cljc.java-time.instant/parse foo))

(defn
 timezone-parse
 [^java.lang.String foo]
 (cljc.java-time.zone-id/of foo))

(defn
 date-now
 ([] (cljc.java-time.local-date/now))
 ([^java.time.Clock clock] (cljc.java-time.local-date/now clock)))

(defn
 monthday-now
 ([] (cljc.java-time.month-day/now))
 ([^java.time.Clock clock] (cljc.java-time.month-day/now clock)))

(defn
 datetime-now
 ([] (cljc.java-time.local-date-time/now))
 ([^java.time.Clock clock] (cljc.java-time.local-date-time/now clock)))

(defn
 time-now
 ([] (cljc.java-time.local-time/now))
 ([^java.time.Clock clock] (cljc.java-time.local-time/now clock)))

(defn
 yearmonth-now
 ([] (cljc.java-time.year-month/now))
 ([^java.time.Clock clock] (cljc.java-time.year-month/now clock)))

(defn
 zdt-now
 ([] (cljc.java-time.zoned-date-time/now))
 ([^java.time.Clock clock] (cljc.java-time.zoned-date-time/now clock)))

(defn
 instant-now
 ([] (cljc.java-time.instant/now))
 ([^java.time.Clock clock] (cljc.java-time.instant/now clock)))

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

