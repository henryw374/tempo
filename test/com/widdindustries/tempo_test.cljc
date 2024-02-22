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
          timezone (str (t/timezone-system-default))
          zdt (t/zdt-from {:datetime datetime :timezone_id timezone})]
      (is (t/zdt? zdt))
      (def zdt zdt)
      ;(is (= datetime (t/zdt->datetime zdt)))
      (is (= timezone (t/zdt->timezone_id zdt)))
      ))
  (testing "level 2"
    (let [date (t/date-now)
          time (t/time-now)
          timezone (str (t/timezone-system-default))
          zdt (t/zdt-from {:date date :time time :timezone_id timezone})]
      (is (t/zdt? zdt))
      (is (= time (t/zdt->time zdt)))
      (is (= date (t/zdt->date zdt)))
      (is (= timezone (t/zdt->timezone_id zdt)))      )
    )
  (testing "level 3"
    (let [ym (t/yearmonth-parse "2020-02")
          timezone (str (t/timezone-parse "Pacific/Honolulu"))
          zdt (t/zdt-from {:yearmonth ym :day-of-month 1 
                           :hour 1 
                           :timezone_id timezone})]
      (is (t/zdt? zdt))
      (is (= (t/yearmonth->year ym) (t/zdt->year zdt)))
      (is (= 1 (t/zdt->day-of-month zdt)))
      (is (= t/weekday-saturday (t/weekday-number->weekday (t/zdt->day-of-week zdt))))
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
               (t/clock-fixed (t/instant-parse "2020-02-02T09:19:42.128946Z") "UTC"))))))

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

(deftest prop-test
  (let [combos [[t/instant-now [t/nanos-property t/micros-property t/millis-property
                                ; not days - mistakenly assumed to be 24hr in java.time
                                ] [t/seconds-property t/hours-property]]
                [t/zdt-now [t/nanos-property t/micros-property t/millis-property
                            t/seconds-property t/hours-property
                            t/days-property t/months-property t/years-property]]
                [t/datetime-now [t/nanos-property t/micros-property t/millis-property
                                 t/seconds-property t/hours-property
                                 t/days-property t/months-property t/years-property]]
                [t/date-now [t/days-property t/months-property t/years-property]]
                [t/yearmonth-now [t/months-property t/years-property]]
                ;[t/monthday-now [t/months-property t/days-property]]
                ]]
    (doseq [[now props xtras] combos
          shiftable-prop (concat props xtras)]
      (let [i-1 (now)
            i-2 (-> i-1
                    (t/>> 1 shiftable-prop))]
        (testing (str "shift until" now " by " shiftable-prop)
          (is (= 1 (t/until i-1 i-2 shiftable-prop)))
          (is (= -1 (t/until i-2 i-1 shiftable-prop))))))
    (doseq [[now props ] combos
            withable-prop props]
      (let [i-1 (now)]
        (is (not= i-1 (t/with i-1 1 withable-prop)))))))


; clock tests
(deftest clock-test
  (let [zone (str (t/timezone-system-default))
        now (t/instant-now)
        fixed (t/clock-fixed now zone)
        offset (t/clock-offset-millis fixed 1)]
    (is (= now (t/instant-now fixed)))
    (is (= (t/>> now (d/duration-parse "PT0.001S")) (t/instant-now offset)))
    (is (t/> (t/instant-now (t/clock-system-default-zone)) (t/instant-now fixed)))
    (is (= (t/zdt->timezone_id (t/zdt-now fixed)) (t/zdt->timezone_id (t/zdt-now offset))))
    ))

(deftest adjust-test
  (testing "adjusting date"
    ;todo
    )
  (testing "adjusting time"
    ;todo - instant
    (doseq [[x hour minute second milli micro nano] [[(t/zdt-parse "2024-02-22T00:00:00Z[Europe/London]")
                                                      t/zdt->hour
                                                      t/zdt->minute
                                                      t/zdt->second
                                                      t/zdt->millisecond
                                                      t/zdt->microsecond
                                                      t/zdt->nanosecond]
                                                     [(t/time-from {:hour 0})
                                                      t/time->hour
                                                      t/time->minute
                                                      t/time->second
                                                      t/time->millisecond
                                                      t/time->microsecond
                                                      t/time->nanosecond]
                                                     [
                                                      (-> (t/datetime-from {:year 1 :month 1 :day-of-month 1})
                                                          ;(t/with 10 t/hours-property)
                                                          )
                                                      t/datetime->hour
                                                      t/datetime->minute
                                                      t/datetime->second
                                                      t/datetime->millisecond
                                                      t/datetime->microsecond
                                                      t/datetime->nanosecond]]]
      (let [
            y (-> x
                  (t/with 10 t/hours-property)
                  (t/with 55 t/minutes-property)
                  (t/with 30 t/seconds-property)
                  (t/with 123 t/millis-property)
                  (t/with 456 t/micros-property)
                  (t/with 789 t/nanos-property))]
        (is (= 10 (hour y)))
        (is (= 55 (minute y)))
        (is (= 30 (second y)))
        (is (= 123 (milli y)))
        (is (= 456 (micro y)))
        (is (= 789 (nano y)))))))




t/max
t/min
t/>=
