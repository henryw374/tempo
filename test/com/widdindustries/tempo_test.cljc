(ns com.widdindustries.tempo-test
  (:require [clojure.test :refer [deftest is testing]]
            [com.widdindustries.tempo :as t]
            [com.widdindustries.tempo.duration-alpha :as d])
  #?(:clj (:import [java.util Date])))

(comment 
  (remove-ns (.name *ns*))
  (remove-ns 'com.widdindustries.tempo)
  (require '[com.widdindustries.tempo] :reload-all)

  )

(t/extend-all-cljs-protocols)
;
(deftest construction-from-parts-test
  (testing "level 0"
    (let [nanos 789
          micros 456
          millis 123]
      ;todo

      )
    )
  (testing "level 1"
    (testing "setting zone on zdt"
      (let [timezone_id "Pacific/Honolulu"
            zdt (t/zdt-now (t/clock-with-zone timezone_id))]
        (testing "roundtrip same instant"
          (is (= zdt (t/zdt-from
                       {:timezone_id timezone_id
                        :instant     (t/instant-from {:zdt zdt})}))))
        (testing "keep wall time, change zone"
          (let [zdt-2 (t/zdt-from {:zdt zdt
                                   :timezone_id "Europe/London"})]
            (is (= (t/zdt->datetime zdt) (t/zdt->datetime zdt-2)))
            (is (not= (t/zdt->timezone_id zdt) (t/zdt->timezone_id zdt-2) ))))))
    (let [datetime (t/datetime-now (t/clock-system-default-zone))
          timezone (str (t/timezone-now (t/clock-system-default-zone)))
          zdt (t/zdt-from {:datetime datetime :timezone_id timezone})]
      (is (t/zdt? zdt))
      (is (= datetime (t/zdt->datetime zdt)))
      (is (= timezone (t/zdt->timezone_id zdt)))
      ))
  (testing "level 2"
    (let [date (t/date-now (t/clock-system-default-zone))
          time (t/time-now (t/clock-system-default-zone))
          timezone (str (t/timezone-now (t/clock-system-default-zone)))
          zdt (t/zdt-from {:date date :time time :timezone_id timezone})]
      (is (t/zdt? zdt))
      (is (= time (t/zdt->time zdt)))
      (is (= date (t/zdt->date zdt)))
      (is (= timezone (t/zdt->timezone_id zdt))))
    )
  (testing "level 3"
    (let [ym (t/yearmonth-parse "2020-02")
          timezone (str (t/timezone-parse "Pacific/Honolulu"))
          zdt (t/zdt-from {:yearmonth   ym :day-of-month 1
                           :hour        1
                           :timezone_id timezone})]
      (is (t/zdt? zdt))
      (is (= (t/yearmonth->year ym) (t/zdt->year zdt)))
      (is (= 1 (t/zdt->day-of-month zdt)))
      (is (= t/weekday-saturday (t/weekday-number->weekday (t/zdt->day-of-week zdt))))
      (is (= 1 (t/zdt->hour zdt)))))
  (testing "level 4"
    (let [zdt (t/zdt-now (t/clock-system-default-zone))]
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
  (is (t/date? (t/date-now (t/clock-system-default-zone))))
  ;todo - test zone that'll return different date to the sys default?
  (is (t/date? (t/date-now (t/clock-system-default-zone))))
  (is (= "2020-02-02"
        (str (t/date-now
               (t/clock-fixed (t/instant-parse "2020-02-02T09:19:42.128946Z") "UTC"))))))

(deftest equals-hash-compare-date
  (let [middle (t/date-now (t/clock-system-default-zone))
        earliest (t/<< middle 1 t/days-property )
        latest (t/>> middle 1 t/days-property )]
    (is (not= middle earliest))
    (is (= middle (t/date-now (t/clock-system-default-zone))))
    ;(compare earliest middle)
    (is (= (sort [latest earliest middle]) [earliest middle latest]))
    (is (= (hash middle) (hash (t/date-now (t/clock-system-default-zone)))))
    (is (not= (hash earliest) (hash (t/date-now (t/clock-system-default-zone)))))))

(deftest preds
  (is (t/date? (t/date-now (t/clock-system-default-zone))))
  (is (not (t/period? (t/date-now (t/clock-system-default-zone))))))

(deftest parsing-test
  (is (t/timezone? (t/timezone-parse "Europe/London"))))

(deftest equals-hash
  (is (= (t/timezone-parse "Europe/London") (t/timezone-parse "Europe/London")))
  (is (not= (t/timezone-parse "Europe/London") (t/timezone-parse "Europe/Paris")))
  (is (= 1 (get {(t/timezone-parse "Europe/London") 1} (t/timezone-parse "Europe/London")))))

(deftest shift
  ;todo - generate for combinations of duration/period and entity
  (let [a-date (t/date-now (t/clock-system-default-zone))
        ;period (d/period-parse "P3D")
        plus3 (t/>> a-date 3 t/days-property)]
    (is (= a-date (t/<< plus3 3 t/days-property)))
    ;todo - also compare  >=, > etc not=, hash not=
    ))

(deftest prop-test
  (let [combos [[t/instant-now [t/nanoseconds-property t/microseconds-property t/milliseconds-property
                                ; not days - mistakenly assumed to be 24hr in java.time
                                ] ;[t/seconds-property t/hours-property]
                 ]
                [t/zdt-now [t/nanoseconds-property t/microseconds-property t/milliseconds-property
                            t/seconds-property t/hours-property
                            t/days-property ;t/months-property t/years-property
                            ]]
                [t/datetime-now [t/nanoseconds-property t/microseconds-property t/milliseconds-property
                                 t/seconds-property t/hours-property
                                 t/days-property ;t/months-property t/years-property
                                 ]]
                [t/date-now [t/days-property ;t/months-property t/years-property
                             ]]
                [t/yearmonth-now [t/months-property t/years-property]]
                ;[t/monthday-now [t/months-property t/days-property]]
                ]]
    (doseq [[now props xtras] combos
            shiftable-prop (concat props xtras)]
      (let [i-1 (now (t/clock-system-default-zone))
            i-2 (-> i-1
                    (t/>> 1 shiftable-prop))]
        (testing (str "shift until" i-1 " by " #?(:clj shiftable-prop :cljs (t/unit-field (t/unit shiftable-prop))))
          (is (= 1 (t/until i-1 i-2 shiftable-prop)))
          (is (= -1 (t/until i-2 i-1 shiftable-prop))))))
    (doseq [[now props] combos
            withable-prop props]
      (let [i-1 (now (t/clock-system-default-zone))
            current (t/get-field i-1 withable-prop)]
        (when-not (t/instant? i-1)
          (testing (str "with " i-1 " prop " (str withable-prop) " current " current 
                     " " #?(:cljs (t/unit-field (t/unit withable-prop))))
            (is (not= i-1 (t/with i-1 (if (= 1 current) 2 1) withable-prop)) (str i-1 " " withable-prop))))))))


;(t/get-field (t/zdt-now (t/clock-system-default-zone)) t/days-property)

(deftest clock-test
  (let [zone (str (t/timezone-now (t/clock-system-default-zone)))
        now (t/instant-now (t/clock-system-default-zone))
        fixed (t/clock-fixed now zone)
        offset (t/clock-offset-millis fixed 1)]
    (is (= now (t/instant-now fixed)))
    (is (= (t/>> now 1 t/milliseconds-property) (t/instant-now offset)))
    (is (t/> (t/instant-now (t/clock-system-default-zone)) (t/instant-now fixed)))
    (is (= (t/zdt->timezone_id (t/zdt-now fixed)) (t/zdt->timezone_id (t/zdt-now offset))))
    ))

(deftest adjust-test
  (testing "adjusting date"
    ;todo
    )
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
    (testing (str "adjusting time " x)
      (let [y (-> x
                  (t/with 10 t/hours-property)
                  (t/with 55 t/minutes-property)
                  (t/with 30 t/seconds-property)
                  (t/with 123 t/milliseconds-property)
                  (t/with 456 t/microseconds-property)
                  (t/with 789 t/nanoseconds-property))]
        (is (= 10 (hour y)))
        (is (= 55 (minute y)))
        (is (= 30 (second y)))
        (is (= 123 (milli y)))
        (is (= 456 (micro y)))
        (is (= 789 (nano y)))))
    (testing (str "range errors on sub-second fields " x)
      (doseq [prop [t/milliseconds-property
                    t/microseconds-property
                    t/nanoseconds-property]]
        (is (thrown? #?(:clj Throwable :cljs js/Error) (-> x (t/with 1000 prop))) (str x " " prop))
        (is (thrown? #?(:clj Throwable :cljs js/Error) (-> x (t/with -1 prop))))))))

(deftest round-trip-legacy
  (let [i (t/instant-parse "2020-02-02T00:00:00Z")]
    (is (= i
          (-> i (t/instant->legacydate) (t/legacydate->instant))))))


(deftest truncate-test
  (doseq [[temporal props] [[(t/zdt-parse "2020-02-02T09:19:42.123456789Z[Europe/London]") [t/days-property t/hours-property t/minutes-property
                                                                             t/seconds-property t/milliseconds-property t/microseconds-property
                                                                             t/nanoseconds-property]]
                            [(t/datetime-parse "2020-02-02T09:19:42.123456789") [t/days-property t/hours-property t/minutes-property
                                                                                 t/seconds-property t/milliseconds-property t/microseconds-property
                                                                                 t/nanoseconds-property]]
                            [(t/time-parse "09:19:42.123456789") [t/hours-property t/minutes-property
                                                                  t/seconds-property t/milliseconds-property t/microseconds-property
                                                                  t/nanoseconds-property]]]
          prop props]
    (is (= (-> (t/truncate temporal prop) (t/get-field prop))
          (t/get-field temporal prop))))
  ;todo 
  #_[(t/instant-parse "2020-02-02T09:19:42.123456789Z") [t/days-property t/hours-property t/minutes-property
                                                         t/seconds-property t/milliseconds-property t/microseconds-property
                                                         t/nanoseconds-property]]

  #_(-> (t/zdt-parse "2020-02-02T09:19:42.123456789Z")
      (t/truncate t/microseconds-property)
      (t/get-field t/microseconds-property))
  )
;todo

(deftest guardrails-test
  (is (thrown? #?(:clj Throwable :cljs js/Error) (t/>> (t/date-parse "2020-02-02") 1 t/years-property))))


t/monthday-from
t/yearmonth-from
t/max
t/min
t/>=
t/coincident?