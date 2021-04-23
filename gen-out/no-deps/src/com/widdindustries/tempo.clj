(ns
 com.widdindustries.tempo
 ""
 (:import (java.time LocalDateTime ZonedDateTime)))

(defn
 datetime->month
 [^java.time.LocalDateTime foo]
 (.getMonthValue foo))

(defn datetime->second [^java.time.LocalDateTime foo] (.getSecond foo))

(defn zdt->date [^java.time.ZonedDateTime foo] (.toLocalDate foo))

(defn datetime->year [^java.time.LocalDateTime foo] (.getYear foo))

(defn date->month [^java.time.LocalDate foo] (.getMonthValue foo))

(defn
 datetime->month
 [^java.time.LocalDateTime foo]
 (.getMonthValue foo))

(defn
 date->day-of-week
 [^java.time.LocalDate foo]
 (.{:wrap #object[com.widdindustries.gen.graph$fn__10237 0x717b163d "com.widdindustries.gen.graph$fn__10237@717b163d"]}
  foo))

(defn zdt->year [^java.time.ZonedDateTime foo] (.getYear foo))

(defn yearmonth->year [^java.time.YearMonth foo] (.getYear foo))

(defn datetime->hour [^java.time.LocalDateTime foo] (.getHour foo))

(defn zdt->yearmonth [^java.time.ZonedDateTime foo] (.toYearMonth foo))

(defn
 monthday->day-of-month
 [^java.time.MonthDay foo]
 (.getDayOfMonth foo))

(defn date->month [^java.time.LocalDate foo] (.getMonthValue foo))

(defn
 datetime->day-of-week
 [^java.time.LocalDateTime foo]
 (.{:wrap #object[com.widdindustries.gen.graph$fn__10237 0x717b163d "com.widdindustries.gen.graph$fn__10237@717b163d"]}
  foo))

(defn zdt->hour [^java.time.ZonedDateTime foo] (.getHour foo))

(defn
 zdt->day-of-week
 [^java.time.ZonedDateTime foo]
 (.{:wrap #object[com.widdindustries.gen.graph$fn__10237 0x717b163d "com.widdindustries.gen.graph$fn__10237@717b163d"]}
  foo))

(defn zdt->instant [^java.time.ZonedDateTime foo] (.toInstant foo))

(defn zdt->nano [^java.time.ZonedDateTime foo] (.getNano foo))

(defn
 datetime->month
 [^java.time.LocalDateTime foo]
 (.getMonthValue foo))

(defn
 datetime->day-of-month
 [^java.time.LocalDateTime foo]
 (.getDayOfMonth foo))

(defn monthday->month [^java.time.MonthDay foo] (.getMonthValue foo))

(defn
 zdt->day-of-month
 [^java.time.ZonedDateTime foo]
 (.getDayOfMonth foo))

(defn zdt->minute [^java.time.ZonedDateTime foo] (.getMinute foo))

(defn zdt->year [^java.time.ZonedDateTime foo] (.getYear foo))

(defn datetime->date [^java.time.LocalDateTime foo] (.toLocalDate foo))

(defn
 date->day-of-month
 [^java.time.LocalDate foo]
 (.getDayOfMonth foo))

(defn zdt->timezone [^java.time.ZonedDateTime foo] (.getZone foo))

(defn date->month [^java.time.LocalDate foo] (.getMonthValue foo))

(defn yearmonth->month [^java.time.YearMonth foo] (.getMonthValue foo))

(defn zdt->second [^java.time.ZonedDateTime foo] (.getSecond foo))

(defn
 datetime->monthday
 [^java.time.LocalDateTime foo]
 (.toMonthDay foo))

(defn date->year [^java.time.LocalDate foo] (.getYear foo))

(defn instant->epochnano [^java.time.Instant foo] (.toEpochNano foo))

(defn instant->epochmilli [^java.time.Instant foo] (.toEpochMilli foo))

(defn time->second [^java.time.LocalTime foo] (.getSecond foo))

(defn
 datetime->day-of-month
 [^java.time.LocalDateTime foo]
 (.getDayOfMonth foo))

(defn date->year [^java.time.LocalDate foo] (.getYear foo))

(defn
 zdt->datetime
 [^java.time.ZonedDateTime foo]
 (.toLocalDateTime foo))

(defn
 date->day-of-month
 [^java.time.LocalDate foo]
 (.getDayOfMonth foo))

(defn zdt->time [^java.time.ZonedDateTime foo] (.toLocalTime foo))

(defn date->monthday [^java.time.LocalDate foo] (.toMonthDay foo))

(defn
 datetime->yearmonth
 [^java.time.LocalDateTime foo]
 (.toYearMonth foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonthValue foo))

(defn
 zdt->day-of-month
 [^java.time.ZonedDateTime foo]
 (.getDayOfMonth foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonthValue foo))

(defn datetime->minute [^java.time.LocalDateTime foo] (.getMinute foo))

(defn time->nano [^java.time.LocalTime foo] (.getNano foo))

(defn zdt->monthday [^java.time.ZonedDateTime foo] (.toMonthDay foo))

(defn time->minute [^java.time.LocalTime foo] (.getMinute foo))

(defn time->hour [^java.time.LocalTime foo] (.getHour foo))

(defn datetime->nano [^java.time.LocalDateTime foo] (.getNano foo))

(defn datetime->year [^java.time.LocalDateTime foo] (.getYear foo))

(defn date->yearmonth [^java.time.LocalDate foo] (.toYearMonth foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonthValue foo))

(defn datetime->time [^java.time.LocalDateTime foo] (.toLocalTime foo))

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
  (LocalTime/of hour minute second nano)))

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

