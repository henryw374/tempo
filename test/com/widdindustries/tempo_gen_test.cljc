^{:line 46, :column 15}
(ns
 com.widdindustries.tempo-gen-test
 ^{:line 47, :column 17}
 (:require
  [clojure.test :refer [deftest is testing]]
  [com.widdindustries.tempo :as t]))

^{:line 51, :column 9} (comment "accessors")

^{:line 53, :column 9} (comment "parsers")

(deftest
 zdt-parse-test
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (= now-now (-> now-now str t/zdt-parse)))))

(deftest
 datetime-parse-test
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (= now-now (-> now-now str t/datetime-parse)))))

(deftest
 date-parse-test
 (let
  [now-now (t/date-now (t/clock-system-default-zone))]
  (is (= now-now (-> now-now str t/date-parse)))))

(deftest
 time-parse-test
 (let
  [now-now (t/time-now (t/clock-system-default-zone))]
  (is (= now-now (-> now-now str t/time-parse)))))

(deftest
 instant-parse-test
 (let
  [now-now (t/instant-now (t/clock-system-default-zone))]
  (is (= now-now (-> now-now str t/instant-parse)))))

(deftest
 monthday-parse-test
 (let
  [now-now (t/monthday-now (t/clock-system-default-zone))]
  (is (= now-now (-> now-now str t/monthday-parse)))))

(deftest
 yearmonth-parse-test
 (let
  [now-now (t/yearmonth-now (t/clock-system-default-zone))]
  (is (= now-now (-> now-now str t/yearmonth-parse)))))

^{:line 55, :column 9} (comment "nowers")

(deftest
 zdt-now-test
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (t/zdt? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-system-default)))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-system-default)))
   now-clock-1
   (t/zdt-now clock-1)
   now-clock-2
   (t/zdt-now clock-2)]
  (is (t/zdt? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 datetime-now-test
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (t/datetime? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-system-default)))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-system-default)))
   now-clock-1
   (t/datetime-now clock-1)
   now-clock-2
   (t/datetime-now clock-2)]
  (is (t/datetime? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 date-now-test
 (let
  [now-now (t/date-now (t/clock-system-default-zone))]
  (is (t/date? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-system-default)))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-system-default)))
   now-clock-1
   (t/date-now clock-1)
   now-clock-2
   (t/date-now clock-2)]
  (is (t/date? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 time-now-test
 (let
  [now-now (t/time-now (t/clock-system-default-zone))]
  (is (t/time? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-system-default)))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-system-default)))
   now-clock-1
   (t/time-now clock-1)
   now-clock-2
   (t/time-now clock-2)]
  (is (t/time? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 instant-now-test
 (let
  [now-now (t/instant-now (t/clock-system-default-zone))]
  (is (t/instant? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-system-default)))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-system-default)))
   now-clock-1
   (t/instant-now clock-1)
   now-clock-2
   (t/instant-now clock-2)]
  (is (t/instant? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 monthday-now-test
 (let
  [now-now (t/monthday-now (t/clock-system-default-zone))]
  (is (t/monthday? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-system-default)))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-system-default)))
   now-clock-1
   (t/monthday-now clock-1)
   now-clock-2
   (t/monthday-now clock-2)]
  (is (t/monthday? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 yearmonth-now-test
 (let
  [now-now (t/yearmonth-now (t/clock-system-default-zone))]
  (is (t/yearmonth? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-system-default)))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-system-default)))
   now-clock-1
   (t/yearmonth-now clock-1)
   now-clock-2
   (t/yearmonth-now clock-2)]
  (is (t/yearmonth? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

^{:line 57, :column 9} (comment "accessors")

(deftest
 zdt->year
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->year now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/zdt->year now-now) t/years-property)))))

(deftest
 datetime->day-of-week
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->day-of-week now-now)))
  nil))

(deftest
 zdt->millisecond
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->millisecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/zdt->millisecond now-now)
     t/milliseconds-property)))))

(deftest
 datetime->second
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->second now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/datetime->second now-now) t/seconds-property)))))

(deftest
 zdt->date
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (t/date? (t/zdt->date now-now)))
  nil))

(deftest
 zdt->microsecond
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->microsecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/zdt->microsecond now-now)
     t/microseconds-property)))))

(deftest
 date->month
 (let
  [now-now (t/date-now (t/clock-system-default-zone))]
  (is (int? (t/date->month now-now)))
  nil))

(deftest
 datetime->millisecond
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->millisecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/datetime->millisecond now-now)
     t/milliseconds-property)))))

(deftest
 date->year
 (let
  [now-now (t/date-now (t/clock-system-default-zone))]
  (is (int? (t/date->year now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/date->year now-now) t/years-property)))))

(deftest
 zdt->month
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->month now-now)))
  nil))

(deftest
 zdt->day-of-month
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->day-of-month now-now)))
  nil))

(deftest
 time->nanosecond
 (let
  [now-now (t/time-now (t/clock-system-default-zone))]
  (is (int? (t/time->nanosecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/time->nanosecond now-now)
     t/nanoseconds-property)))))

(deftest
 datetime->microsecond
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->microsecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/datetime->microsecond now-now)
     t/microseconds-property)))))

(deftest
 zdt->second
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->second now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/zdt->second now-now) t/seconds-property)))))

(deftest
 datetime->minute
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->minute now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/datetime->minute now-now) t/minutes-property)))))

(deftest
 zdt->nanosecond
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->nanosecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/zdt->nanosecond now-now)
     t/nanoseconds-property)))))

(deftest
 zdt->hour
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->hour now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/zdt->hour now-now) t/hours-property)))))

(deftest
 time->millisecond
 (let
  [now-now (t/time-now (t/clock-system-default-zone))]
  (is (int? (t/time->millisecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/time->millisecond now-now)
     t/milliseconds-property)))))

(deftest
 zdt->instant
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (t/instant? (t/zdt->instant now-now)))
  nil))

(deftest
 datetime->month
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->month now-now)))
  nil))

(deftest
 datetime->day-of-month
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->day-of-month now-now)))
  nil))

(deftest
 time->microsecond
 (let
  [now-now (t/time-now (t/clock-system-default-zone))]
  (is (int? (t/time->microsecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/time->microsecond now-now)
     t/microseconds-property)))))

(deftest
 datetime->date
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (t/date? (t/datetime->date now-now)))
  nil))

(deftest
 zdt->day-of-week
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->day-of-week now-now)))
  nil))

(deftest
 time->second
 (let
  [now-now (t/time-now (t/clock-system-default-zone))]
  (is (int? (t/time->second now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/time->second now-now) t/seconds-property)))))

(deftest
 yearmonth->year
 (let
  [now-now (t/yearmonth-now (t/clock-system-default-zone))]
  (is (int? (t/yearmonth->year now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/yearmonth->year now-now) t/years-property)))))

(deftest
 datetime->year
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->year now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/datetime->year now-now) t/years-property)))))

(deftest
 monthday->day-of-month
 (let
  [now-now (t/monthday-now (t/clock-system-default-zone))]
  (is (int? (t/monthday->day-of-month now-now)))
  nil))

(deftest
 yearmonth->month
 (let
  [now-now (t/yearmonth-now (t/clock-system-default-zone))]
  (is (int? (t/yearmonth->month now-now)))
  nil))

(deftest
 datetime->nanosecond
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->nanosecond now-now)))
  (is
   (=
    now-now
    (t/with
     now-now
     (t/datetime->nanosecond now-now)
     t/nanoseconds-property)))))

(deftest
 datetime->hour
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (int? (t/datetime->hour now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/datetime->hour now-now) t/hours-property)))))

(deftest
 date->day-of-month
 (let
  [now-now (t/date-now (t/clock-system-default-zone))]
  (is (int? (t/date->day-of-month now-now)))
  nil))

(deftest
 zdt->datetime
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (t/datetime? (t/zdt->datetime now-now)))
  nil))

(deftest
 time->minute
 (let
  [now-now (t/time-now (t/clock-system-default-zone))]
  (is (int? (t/time->minute now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/time->minute now-now) t/minutes-property)))))

(deftest
 time->hour
 (let
  [now-now (t/time-now (t/clock-system-default-zone))]
  (is (int? (t/time->hour now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/time->hour now-now) t/hours-property)))))

(deftest
 zdt->time
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (t/time? (t/zdt->time now-now)))
  nil))

(deftest
 zdt->timezone_id
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (string? (t/zdt->timezone_id now-now)))
  nil))

(deftest
 monthday->month
 (let
  [now-now (t/monthday-now (t/clock-system-default-zone))]
  (is (int? (t/monthday->month now-now)))
  nil))

(deftest
 date->day-of-week
 (let
  [now-now (t/date-now (t/clock-system-default-zone))]
  (is (int? (t/date->day-of-week now-now)))
  nil))

(deftest
 zdt->minute
 (let
  [now-now (t/zdt-now (t/clock-system-default-zone))]
  (is (int? (t/zdt->minute now-now)))
  (is
   (=
    now-now
    (t/with now-now (t/zdt->minute now-now) t/minutes-property)))))

(deftest
 instant->legacydate
 (let
  [now-now (t/instant-now (t/clock-system-default-zone))]
  (is (t/legacydate? (t/instant->legacydate now-now)))
  nil))

(deftest
 instant->epochmilli
 (let
  [now-now (t/instant-now (t/clock-system-default-zone))]
  (is (int? (t/instant->epochmilli now-now)))
  nil))

(deftest
 datetime->time
 (let
  [now-now (t/datetime-now (t/clock-system-default-zone))]
  (is (t/time? (t/datetime->time now-now)))
  nil))

