(ns
 com.widdindustries.tempo
 ""
 (:import (java.time LocalDateTime ZonedDateTime)))

(defn
 date->day-of-month
 [^java.time.LocalDate foo]
 (.getDayOfMonth foo))

(defn date->day-of-week [^java.time.LocalDate foo] (.getDayOfWeek foo))

(defn date->month [^java.time.LocalDate foo] (.getMonth foo))

(defn date->month [^java.time.LocalDate foo] (.getMonth foo))

(defn
 date->day-of-month
 [^java.time.LocalDate foo]
 (.getDayOfMonth foo))

(defn date->monthday [^java.time.LocalDate foo] (.toMonthDay foo))

(defn date->year [^java.time.LocalDate foo] (.getYear foo))

(defn date->yearmonth [^java.time.LocalDate foo] (.toYearMonth foo))

(defn
 datetime->yearmonth
 [^java.time.LocalDateTime foo]
 (.toYearMonth foo))

(defn
 datetime->monthday
 [^java.time.LocalDateTime foo]
 (.toMonthDay foo))

(defn datetime->month [^java.time.LocalDateTime foo] (.getMonth foo))

(defn datetime->month [^java.time.LocalDateTime foo] (.getMonth foo))

(defn
 datetime->day-of-month
 [^java.time.LocalDateTime foo]
 (.getDayOfMonth foo))

(defn
 datetime->day-of-week
 [^java.time.LocalDateTime foo]
 (.getDayOfWeek foo))

(defn datetime->year [^java.time.LocalDateTime foo] (.getYear foo))

(defn datetime->date [^java.time.LocalDateTime foo] (.toLocalDate foo))

(defn
 datetime->day-of-month
 [^java.time.LocalDateTime foo]
 (.getDayOfMonth foo))

(defn datetime->second [^java.time.LocalDateTime foo] (.getSecond foo))

(defn datetime->nano [^java.time.LocalDateTime foo] (.getNano foo))

(defn datetime->minute [^java.time.LocalDateTime foo] (.getMinute foo))

(defn datetime->time [^java.time.LocalDateTime foo] (.toLocalTime foo))

(defn datetime->hour [^java.time.LocalDateTime foo] (.getHour foo))

(defn
 instant->epochmillis
 [^java.time.Instant foo]
 (.getEpochmillis foo))

(defn instant->epochnanos [^java.time.Instant foo] (.getEpochnanos foo))

(defn
 monthday->day-of-month
 [^java.time.MonthDay foo]
 (.getDayOfMonth foo))

(defn monthday->month [^java.time.MonthDay foo] (.getMonth foo))

(defn time->hour [^java.time.LocalTime foo] (.getHour foo))

(defn time->minute [^java.time.LocalTime foo] (.getMinute foo))

(defn time->nano [^java.time.LocalTime foo] (.getNano foo))

(defn time->second [^java.time.LocalTime foo] (.getSecond foo))

(defn zdt->nano [^java.time.ZonedDateTime foo] (.getNano foo))

(defn zdt->second [^java.time.ZonedDateTime foo] (.getSecond foo))

(defn zdt->yearmonth [^java.time.ZonedDateTime foo] (.toYearMonth foo))

(defn zdt->time [^java.time.ZonedDateTime foo] (.toLocalTime foo))

(defn zdt->minute [^java.time.ZonedDateTime foo] (.getMinute foo))

(defn zdt->year [^java.time.ZonedDateTime foo] (.getYear foo))

(defn zdt->hour [^java.time.ZonedDateTime foo] (.getHour foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonth foo))

(defn
 zdt->day-of-month
 [^java.time.ZonedDateTime foo]
 (.getDayOfMonth foo))

(defn
 zdt->day-of-week
 [^java.time.ZonedDateTime foo]
 (.getDayOfWeek foo))

(defn zdt->monthday [^java.time.ZonedDateTime foo] (.toMonthDay foo))

(defn
 zdt->day-of-month
 [^java.time.ZonedDateTime foo]
 (.getDayOfMonth foo))

(defn zdt->month [^java.time.ZonedDateTime foo] (.getMonth foo))

(defn
 zdt->datetime
 [^java.time.ZonedDateTime foo]
 (.toLocalDateTime foo))

(defn zdt->date [^java.time.ZonedDateTime foo] (.toLocalDate foo))

(defn zdt->instant [^java.time.ZonedDateTime foo] (.toInstant foo))

(defn zdt->timezone [^java.time.ZonedDateTime foo] (.toZoneId foo))

(defn datetime-from [thing])

(defn
 zdt-from
 [thing]
 (let
  [ldt
   (or (get thing :datetime) (datetime-from thing))
   zone
   (get thing :timezone)]
  (ZonedDateTime/of ^LocalDateTime ldt ^ZoneId zone)))

