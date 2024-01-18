(ns com.widdindustries.tempo-test
  (:require [clojure.test :refer [deftest is testing]]
            [com.widdindustries.tempo :as t]
            [com.widdindustries.tempo.duration-alpha :as d])
  #?(:clj (:import [java.util Date])))

 (t/extend-all-cljs-protocols)
;
(deftest construction-from-parts-test
  (testing "level 1"
    (let [datetime (t/datetime-now)
          timezone (t/zone-system-default)
          zdt (t/zdt-from {:datetime datetime :timezone timezone})]
      (is (t/zdt? zdt))
      (def zdt zdt)
      ;(is (= datetime (t/zdt->datetime zdt)))
      (is (= timezone (t/zdt->timezone zdt)))
      ))
  (testing "level 2"
    (let [date (t/date-now)
          time (t/time-now)
          timezone (t/zone-system-default)
          zdt (t/zdt-from {:date date :time time :timezone timezone})]
      (is (t/zdt? zdt))
      (is (= time (t/zdt->time zdt)))
      (is (= date (t/zdt->date zdt)))
      (is (= timezone (t/zdt->timezone zdt)))      )
    )
  (testing "level 3"
    (let [ym (t/yearmonth-now)
          timezone (t/zone-system-default)
          zdt (t/zdt-from {:yearmonth ym :day-of-month 1 
                           :hour 1 
                           :timezone timezone})]
      (is (t/zdt? zdt))
      (is (= (t/yearmonth->year ym) (t/zdt->year zdt)))
      (is (= 1 (t/zdt->day-of-month zdt)))
      (is (= 1 (t/zdt->hour zdt)))))
  (testing "level 4"
    (let [zdt (t/zdt-now)]
      (is (t/instant? (t/instant-from {:zdt zdt})))
      (let [i (t/instant-parse "2024-01-16T12:43:44.196000Z")]
        (is (= i (t/instant-from {:epochmilli (t/instant->epochmilli i)}))))
      (let [d #?(:clj (Date.) :cljs (js/Date.))
            i (t/instant-from {:legacydate d})]
        (= (.getTime d) (t/instant->epochmilli i))
        ))))

(deftest parsing-duration
  (is (t/duration? (d/duration-parse "PT1S")))) 

(deftest equals-hash-compare-duration
  (let [make-middle #(d/duration-parse "PT1S")
        middle (make-middle)
        smallest (d/duration->negated (d/duration-parse "PT1S"))
        largest (d/duration-parse "PT2S")]
    (is (not= middle smallest))
    (is (= middle (make-middle)))
    (is (= (sort [largest smallest middle]) [smallest middle largest]))
    (is (= (hash middle) (hash (make-middle))))
    (is (not= (hash smallest) (hash (make-middle))))))

(deftest now-date
  (is (t/date? (t/date-now)))
  ;todo - test zone that'll return different date to the sys default?
  (is (t/date? (t/date-now (t/clock-system-default-zone))))
  (is (= "2020-02-02"
        (str (t/date-now
               (t/clock-fixed (t/instant-parse "2020-02-02T09:19:42.128946Z")
                 (t/timezone-parse "UTC")))))))

(deftest equals-hash-compare-date
  (let [middle (t/date-now)
        earliest (t/<< middle (d/period-parse "P1D"))
        latest (t/>> middle (d/period-parse "P1D"))]
    (is (not= middle earliest))
    (is (= middle (t/date-now)))
    ;(compare earliest middle)
    (is (= (sort [latest earliest middle]) [earliest middle latest]))
    (is (= (hash middle) (hash (t/date-now))))
    (is (not= (hash earliest) (hash (t/date-now))))))

(deftest preds
  (is (t/date? (t/date-now)))
  (is (not (t/period? (t/date-now)))))

(deftest parsing-test
  (is (t/timezone? (t/timezone-parse "Europe/London"))))

(deftest equals-hash
  (is (= (t/timezone-parse "Europe/London") (t/timezone-parse "Europe/London")))
  (is (not= (t/timezone-parse "Europe/London") (t/timezone-parse "Europe/Paris")))
  (is (= 1 (get {(t/timezone-parse "Europe/London") 1} (t/timezone-parse "Europe/London")))))

(deftest shift
  ;todo - generate for combinations of duration/period and entity
  (let [a-date (t/date-now)
        period (d/period-parse "P3D")
        plus3 (t/>> a-date period)]
    (is (= a-date (t/<< plus3 period)))))

(deftest shift-until-test 
  ;todo - generate these for combinations of entity and unit
  (let [i-1 (t/instant-now)
        i-2 (-> i-1
                (t/>> 1 t/days-unit))]
    (= 1 (t/until i-1 i-2 t/days-unit))
    (= -1 (t/until i-2 i-1 t/days-unit))))

;todo - exhaustively test the following

(-> (t/date-now) (t/date->day-of-week)
    t/weekday-number->weekday)
t/weekday-friday
(get t/weekday-number->weekday 4)
(-> (t/zdt-now) (t/zdt->timezone))
; clock tests
(t/clock-fixed (t/instant-now) (t/zone-system-default))
(t/clock-system-default-zone)
;(t/clock-offset)
