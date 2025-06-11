^{:line 47, :column 15}
(ns
 com.widdindustries.tempo-gen-test
 ^{:line 48, :column 17}
 (:require
  [clojure.test :refer [deftest is testing]]
  [com.widdindustries.tempo :as t]))

^{:line 52, :column 9} (comment "accessors")

^{:line 54, :column 9} (comment "parsers")

(deftest
 zdt-parse-test
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (= now-deref (-> now-deref str t/zdt-parse)))))

(deftest
 datetime-parse-test
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (= now-deref (-> now-deref str t/datetime-parse)))))

(deftest
 date-parse-test
 (let
  [now-deref (t/date-deref (t/clock-system-default-zone))]
  (is (= now-deref (-> now-deref str t/date-parse)))))

(deftest
 monthday-parse-test
 (let
  [now-deref (t/monthday-deref (t/clock-system-default-zone))]
  (is (= now-deref (-> now-deref str t/monthday-parse)))))

(deftest
 time-parse-test
 (let
  [now-deref (t/time-deref (t/clock-system-default-zone))]
  (is (= now-deref (-> now-deref str t/time-parse)))))

(deftest
 instant-parse-test
 (let
  [now-deref (t/instant-deref (t/clock-system-default-zone))]
  (is (= now-deref (-> now-deref str t/instant-parse)))))

(deftest
 yearmonth-parse-test
 (let
  [now-deref (t/yearmonth-deref (t/clock-system-default-zone))]
  (is (= now-deref (-> now-deref str t/yearmonth-parse)))))

^{:line 56, :column 9} (comment "derefs")

(deftest
 zdt-deref-test
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (t/zdt? now-deref)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   now-clock-1
   (t/zdt-deref clock-1)
   now-clock-2
   (t/zdt-deref clock-2)]
  (is (t/zdt? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 datetime-deref-test
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (t/datetime? now-deref)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   now-clock-1
   (t/datetime-deref clock-1)
   now-clock-2
   (t/datetime-deref clock-2)]
  (is (t/datetime? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 date-deref-test
 (let
  [now-deref (t/date-deref (t/clock-system-default-zone))]
  (is (t/date? now-deref)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   now-clock-1
   (t/date-deref clock-1)
   now-clock-2
   (t/date-deref clock-2)]
  (is (t/date? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 monthday-deref-test
 (let
  [now-deref (t/monthday-deref (t/clock-system-default-zone))]
  (is (t/monthday? now-deref)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   now-clock-1
   (t/monthday-deref clock-1)
   now-clock-2
   (t/monthday-deref clock-2)]
  (is (t/monthday? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 time-deref-test
 (let
  [now-deref (t/time-deref (t/clock-system-default-zone))]
  (is (t/time? now-deref)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   now-clock-1
   (t/time-deref clock-1)
   now-clock-2
   (t/time-deref clock-2)]
  (is (t/time? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 instant-deref-test
 (let
  [now-deref (t/instant-deref (t/clock-system-default-zone))]
  (is (t/instant? now-deref)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   now-clock-1
   (t/instant-deref clock-1)
   now-clock-2
   (t/instant-deref clock-2)]
  (is (t/instant? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 yearmonth-deref-test
 (let
  [now-deref (t/yearmonth-deref (t/clock-system-default-zone))]
  (is (t/yearmonth? now-deref)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (str (t/timezone-deref (t/clock-system-default-zone))))
   now-clock-1
   (t/yearmonth-deref clock-1)
   now-clock-2
   (t/yearmonth-deref clock-2)]
  (is (t/yearmonth? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

^{:line 58, :column 9} (comment "accessors")

(deftest
 zdt->year
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->year now-deref)))
  nil))

(deftest
 datetime->month
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->month now-deref)))
  nil))

(deftest
 datetime->day-of-week
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->day-of-week now-deref)))
  nil))

(deftest
 datetime->microsecond
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->microsecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/datetime->microsecond now-deref)
     t/microseconds-property)))))

(deftest
 datetime->second
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->second now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/datetime->second now-deref)
     t/seconds-property)))))

(deftest
 zdt->date
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (t/date? (t/zdt->date now-deref)))
  nil))

(deftest
 zdt->timezone
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (string? (t/zdt->timezone now-deref)))
  nil))

(deftest
 datetime->monthday
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (t/monthday? (t/datetime->monthday now-deref)))
  nil))

(deftest
 datetime->yearmonth
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (t/yearmonth? (t/datetime->yearmonth now-deref)))
  nil))

(deftest
 yearmonth->year
 (let
  [now-deref (t/yearmonth-deref (t/clock-system-default-zone))]
  (is (int? (t/yearmonth->year now-deref)))
  nil))

(deftest
 zdt->millisecond
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->millisecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/zdt->millisecond now-deref)
     t/milliseconds-property)))))

(deftest
 zdt->nanosecond
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->nanosecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/zdt->nanosecond now-deref)
     t/nanoseconds-property)))))

(deftest
 zdt->second
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->second now-deref)))
  (is
   (=
    now-deref
    (t/with now-deref (t/zdt->second now-deref) t/seconds-property)))))

(deftest
 datetime->minute
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->minute now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/datetime->minute now-deref)
     t/minutes-property)))))

(deftest
 datetime->day-of-month
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->day-of-month now-deref)))
  nil))

(deftest
 zdt->hour
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->hour now-deref)))
  (is
   (=
    now-deref
    (t/with now-deref (t/zdt->hour now-deref) t/hours-property)))))

(deftest
 zdt->instant
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (t/instant? (t/zdt->instant now-deref)))
  nil))

(deftest
 zdt->month
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->month now-deref)))
  nil))

(deftest
 zdt->monthday
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (t/monthday? (t/zdt->monthday now-deref)))
  nil))

(deftest
 monthday->month
 (let
  [now-deref (t/monthday-deref (t/clock-system-default-zone))]
  (is (int? (t/monthday->month now-deref)))
  nil))

(deftest
 datetime->date
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (t/date? (t/datetime->date now-deref)))
  nil))

(deftest
 zdt->microsecond
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->microsecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/zdt->microsecond now-deref)
     t/microseconds-property)))))

(deftest
 zdt->day-of-month
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->day-of-month now-deref)))
  nil))

(deftest
 date->monthday
 (let
  [now-deref (t/date-deref (t/clock-system-default-zone))]
  (is (t/monthday? (t/date->monthday now-deref)))
  nil))

(deftest
 zdt->day-of-week
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->day-of-week now-deref)))
  nil))

(deftest
 time->second
 (let
  [now-deref (t/time-deref (t/clock-system-default-zone))]
  (is (int? (t/time->second now-deref)))
  (is
   (=
    now-deref
    (t/with now-deref (t/time->second now-deref) t/seconds-property)))))

(deftest
 date->month
 (let
  [now-deref (t/date-deref (t/clock-system-default-zone))]
  (is (int? (t/date->month now-deref)))
  nil))

(deftest
 instant->legacydate
 (let
  [now-deref (t/instant-deref (t/clock-system-default-zone))]
  (is (t/legacydate? (t/instant->legacydate now-deref)))
  nil))

(deftest
 date->year
 (let
  [now-deref (t/date-deref (t/clock-system-default-zone))]
  (is (int? (t/date->year now-deref)))
  nil))

(deftest
 datetime->nanosecond
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->nanosecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/datetime->nanosecond now-deref)
     t/nanoseconds-property)))))

(deftest
 datetime->year
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->year now-deref)))
  nil))

(deftest
 instant->epochmilli
 (let
  [now-deref (t/instant-deref (t/clock-system-default-zone))]
  (is (int? (t/instant->epochmilli now-deref)))
  nil))

(deftest
 datetime->hour
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->hour now-deref)))
  (is
   (=
    now-deref
    (t/with now-deref (t/datetime->hour now-deref) t/hours-property)))))

(deftest
 date->day-of-month
 (let
  [now-deref (t/date-deref (t/clock-system-default-zone))]
  (is (int? (t/date->day-of-month now-deref)))
  nil))

(deftest
 zdt->datetime
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (t/datetime? (t/zdt->datetime now-deref)))
  nil))

(deftest
 time->microsecond
 (let
  [now-deref (t/time-deref (t/clock-system-default-zone))]
  (is (int? (t/time->microsecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/time->microsecond now-deref)
     t/microseconds-property)))))

(deftest
 time->nanosecond
 (let
  [now-deref (t/time-deref (t/clock-system-default-zone))]
  (is (int? (t/time->nanosecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/time->nanosecond now-deref)
     t/nanoseconds-property)))))

(deftest
 time->minute
 (let
  [now-deref (t/time-deref (t/clock-system-default-zone))]
  (is (int? (t/time->minute now-deref)))
  (is
   (=
    now-deref
    (t/with now-deref (t/time->minute now-deref) t/minutes-property)))))

(deftest
 time->hour
 (let
  [now-deref (t/time-deref (t/clock-system-default-zone))]
  (is (int? (t/time->hour now-deref)))
  (is
   (=
    now-deref
    (t/with now-deref (t/time->hour now-deref) t/hours-property)))))

(deftest
 zdt->time
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (t/time? (t/zdt->time now-deref)))
  nil))

(deftest
 monthday->day-of-month
 (let
  [now-deref (t/monthday-deref (t/clock-system-default-zone))]
  (is (int? (t/monthday->day-of-month now-deref)))
  nil))

(deftest
 date->day-of-week
 (let
  [now-deref (t/date-deref (t/clock-system-default-zone))]
  (is (int? (t/date->day-of-week now-deref)))
  nil))

(deftest
 zdt->minute
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (int? (t/zdt->minute now-deref)))
  (is
   (=
    now-deref
    (t/with now-deref (t/zdt->minute now-deref) t/minutes-property)))))

(deftest
 datetime->millisecond
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (int? (t/datetime->millisecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/datetime->millisecond now-deref)
     t/milliseconds-property)))))

(deftest
 zdt->yearmonth
 (let
  [now-deref (t/zdt-deref (t/clock-system-default-zone))]
  (is (t/yearmonth? (t/zdt->yearmonth now-deref)))
  nil))

(deftest
 yearmonth->month
 (let
  [now-deref (t/yearmonth-deref (t/clock-system-default-zone))]
  (is (int? (t/yearmonth->month now-deref)))
  nil))

(deftest
 time->millisecond
 (let
  [now-deref (t/time-deref (t/clock-system-default-zone))]
  (is (int? (t/time->millisecond now-deref)))
  (is
   (=
    now-deref
    (t/with
     now-deref
     (t/time->millisecond now-deref)
     t/milliseconds-property)))))

(deftest
 datetime->time
 (let
  [now-deref (t/datetime-deref (t/clock-system-default-zone))]
  (is (t/time? (t/datetime->time now-deref)))
  nil))

(deftest
 date->yearmonth
 (let
  [now-deref (t/date-deref (t/clock-system-default-zone))]
  (is (t/yearmonth? (t/date->yearmonth now-deref)))
  nil))

