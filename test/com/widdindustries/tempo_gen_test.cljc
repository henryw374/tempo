^{:line 45, :column 15}
(ns
 com.widdindustries.tempo-gen-test
 ^{:line 46, :column 17}
 (:require
  [clojure.test :refer [deftest is testing]]
  [com.widdindustries.tempo :as t]))

^{:line 50, :column 9} (comment "accessors")

^{:line 52, :column 9} (comment "parsers")

(deftest
 datetime-parse-test
 (let
  [now-now (t/datetime-now)]
  (is (= now-now (-> now-now str t/datetime-parse)))))

(deftest
 time-parse-test
 (let
  [now-now (t/time-now)]
  (is (= now-now (-> now-now str t/time-parse)))))

(deftest
 zdt-parse-test
 (let
  [now-now (t/zdt-now)]
  (is (= now-now (-> now-now str t/zdt-parse)))))

(deftest
 date-parse-test
 (let
  [now-now (t/date-now)]
  (is (= now-now (-> now-now str t/date-parse)))))

(deftest
 instant-parse-test
 (let
  [now-now (t/instant-now)]
  (is (= now-now (-> now-now str t/instant-parse)))))

(deftest
 yearmonth-parse-test
 (let
  [now-now (t/yearmonth-now)]
  (is (= now-now (-> now-now str t/yearmonth-parse)))))

(deftest
 monthday-parse-test
 (let
  [now-now (t/monthday-now)]
  (is (= now-now (-> now-now str t/monthday-parse)))))

(deftest
 timezone-parse-test
 (is
  (=
   (t/zone-system-default)
   (-> (t/zone-system-default) str t/timezone-parse))))

^{:line 54, :column 9} (comment "nowers")

(deftest
 datetime-now-test
 (let [now-now (t/datetime-now)] (is (t/datetime? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (t/zone-system-default))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (t/zone-system-default))
   now-clock-1
   (t/datetime-now clock-1)
   now-clock-2
   (t/datetime-now clock-2)]
  (is (t/datetime? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 time-now-test
 (let [now-now (t/time-now)] (is (t/time? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (t/zone-system-default))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (t/zone-system-default))
   now-clock-1
   (t/time-now clock-1)
   now-clock-2
   (t/time-now clock-2)]
  (is (t/time? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 zdt-now-test
 (let [now-now (t/zdt-now)] (is (t/zdt? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (t/zone-system-default))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (t/zone-system-default))
   now-clock-1
   (t/zdt-now clock-1)
   now-clock-2
   (t/zdt-now clock-2)]
  (is (t/zdt? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 date-now-test
 (let [now-now (t/date-now)] (is (t/date? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (t/zone-system-default))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (t/zone-system-default))
   now-clock-1
   (t/date-now clock-1)
   now-clock-2
   (t/date-now clock-2)]
  (is (t/date? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 instant-now-test
 (let [now-now (t/instant-now)] (is (t/instant? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (t/zone-system-default))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (t/zone-system-default))
   now-clock-1
   (t/instant-now clock-1)
   now-clock-2
   (t/instant-now clock-2)]
  (is (t/instant? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 yearmonth-now-test
 (let [now-now (t/yearmonth-now)] (is (t/yearmonth? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (t/zone-system-default))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (t/zone-system-default))
   now-clock-1
   (t/yearmonth-now clock-1)
   now-clock-2
   (t/yearmonth-now clock-2)]
  (is (t/yearmonth? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

(deftest
 monthday-now-test
 (let [now-now (t/monthday-now)] (is (t/monthday? now-now)))
 (let
  [clock-1
   (t/clock-fixed
    (t/instant-parse "1955-11-01T16:46:08.017143Z")
    (t/zone-system-default))
   clock-2
   (t/clock-fixed
    (t/instant-parse "1955-12-02T17:46:08.017143Z")
    (t/zone-system-default))
   now-clock-1
   (t/monthday-now clock-1)
   now-clock-2
   (t/monthday-now clock-2)]
  (is (t/monthday? now-clock-1))
  (is (t/> now-clock-2 now-clock-1))))

