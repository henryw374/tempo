(ns com.widdindustries.tempo-test
  (:require [clojure.test :refer [deftest is testing]]
            [com.widdindustries.tempo :as t]
            [time-literals.read-write])
  #?(:clj (:import [java.util Date])))


(defn ^:export initialise []
  (t/enable-comparison-for-all-temporal-entities)
  (time-literals.read-write/print-time-literals-clj!)
  (time-literals.read-write/print-time-literals-cljs!))

(comment
  (remove-ns (.name *ns*))
  (remove-ns 'com.widdindustries.tempo)
  (require '[com.widdindustries.tempo] :reload-all)
  (require '[time-literals.read-write])
  #time/date "2020-02-02"

  )

 

(deftest construction-from-parts-test ;

  (testing ""
    t/monthday-from
    t/yearmonth-from
    )
  ;todo
  (testing "level 0"
    (let [nanos 789
          micros 456
          millis 123]
      ;todo

      )
    )
  (testing "level 1"
    (testing "setting zone on zdt"
      (let [timezone "Pacific/Honolulu"
            zdt (t/zdt-deref (t/clock-with-timezone timezone))]
        (testing "roundtrip same instant"
          (is (= zdt (t/zdt-from
                       {:timezone timezone
                        :instant     (t/instant-from {:zdt zdt})}))))
        (testing "keep wall time, change zone"
          (let [zdt-2 (t/zdt-from {:zdt         zdt
                                   :timezone "Europe/London"})]
            (is (= (t/zdt->datetime zdt) (t/zdt->datetime zdt-2)))
            (is (not= (t/zdt->timezone zdt) (t/zdt->timezone zdt-2)))))))
    (let [datetime (t/datetime-deref (t/clock-system-default-zone))
          timezone (str (t/timezone-deref (t/clock-system-default-zone)))
          zdt (t/zdt-from {:datetime datetime :timezone timezone})]
      (is (t/zdt? zdt))
      (is (= datetime (t/zdt->datetime zdt)))
      (is (= timezone (t/zdt->timezone zdt)))))
  (testing "level 2"
    (let [date (t/date-deref (t/clock-system-default-zone))
          time (t/time-deref (t/clock-system-default-zone))
          timezone (str (t/timezone-deref (t/clock-system-default-zone)))
          zdt (t/zdt-from {:date date :time time :timezone timezone})]
      (is (t/zdt? zdt))
      (is (= time (t/zdt->time zdt)))
      (is (= date (t/zdt->date zdt)))
      (is (= timezone (t/zdt->timezone zdt))))
    )
  (testing "level 3"
    (let [ym (t/yearmonth-parse "2020-02")
          timezone  "Pacific/Honolulu"
          zdt (t/zdt-from {:yearmonth   ym :day-of-month 1
                           :hour        1
                           :timezone timezone})]
      (is (t/zdt? zdt))
      (is (= (t/yearmonth->year ym) (t/zdt->year zdt)))
      (is (= 1 (t/zdt->day-of-month zdt)))
      (is (= t/weekday-saturday-name (get t/weekday->weekday-name (t/zdt->day-of-week zdt))))
      (is (= 1 (t/zdt->hour zdt)))))
  (testing "level 4"
    (let [zdt (t/zdt-deref (t/clock-system-default-zone))]
      (is (t/instant? (t/instant-from {:zdt zdt})))
      (let [i (t/instant-parse "2024-01-16T12:43:44.196000Z")]
        (is (= i (t/instant-from {:epochmilli (t/instant->epochmilli i)}))))
      (let [d #?(:clj (Date.) :cljs (js/Date.))
            i (t/instant-from {:legacydate d})]
        (is (= (.getTime d) (t/instant->epochmilli i)))
        (is (= i (-> i (t/instant->legacydate) (t/legacydate->instant))))
        (is (= (.getTime d) (-> (.getTime d) (t/epochmilli->instant) (t/instant->epochmilli))))))
    (testing "zdt-instant"
      (let [i (t/instant-parse "2024-01-16T12:43:44.196000Z")]
        (is (= i (-> i (t/instant->zdt-in-UTC) (t/zdt->instant))))))
    (testing "zdt with offset"
      (is (= "+05:50"
            (->
              (t/zdt-from {:instant     (t/instant-deref (t/clock-system-default-zone))
                           :timezone "+05:50"})
              (t/zdt->timezone)))))))

#_(deftest parsing-duration
  (is (t/duration? (d/duration-parse "PT1S"))))

#_(deftest equals-hash-compare-duration
  (let [make-middle #(d/duration-parse "PT1S")
        middle (make-middle)
        smallest (d/duration-parse "PT0S")
        largest (d/duration-parse "PT2S")]
    (is (not= middle smallest))
    (is (= middle (make-middle)))
    (is (= (sort [largest smallest middle]) [smallest middle largest]))
    (is (= (hash middle) (hash (make-middle))))
    (is (not= (hash smallest) (hash (make-middle))))))

(deftest now-date
  (is (t/date? (t/date-deref (t/clock-system-default-zone))))
  ;todo - test zone that'll return different date to the sys default?
  (is (t/date? (t/date-deref (t/clock-system-default-zone))))
  (is (= "2020-02-02"
        (str (t/date-deref
               (t/clock-fixed (t/instant-parse "2020-02-02T09:19:42.128946Z") "UTC"))))))

(deftest equals-hash-compare-date
  (let [middle (t/date-deref (t/clock-system-default-zone))
        earliest (t/<< middle 1 t/days-property)
        latest (t/>> middle 1 t/days-property)]
    (is (not= middle earliest))
    (is (= middle (t/date-deref (t/clock-system-default-zone))))
    ;(compare earliest middle)
    (is (= (sort [latest earliest middle]) [earliest middle latest]))
    (is (= (hash middle) (hash (t/date-deref (t/clock-system-default-zone)))))
    (is (not= (hash earliest) (hash (t/date-deref (t/clock-system-default-zone)))))))

(deftest preds
  (is (t/date? (t/date-deref (t/clock-system-default-zone))))
  (is (not (t/zdt? (t/date-deref (t/clock-system-default-zone))))))

(deftest parsing-test
  (is (t/date? (t/date-parse "2020-02-02"))))

(deftest equals-hash
  (is (= (t/date-parse "2020-02-02") (t/date-parse "2020-02-02")))
  (is (= 1 (get {(t/date-parse "2020-02-02") 1} (t/date-parse "2020-02-02")))))

(deftest shift
  ;todo - generate for combinations of duration/period and entity
  (let [a-date (t/date-deref (t/clock-system-default-zone))
        ;period (d/period-parse "P3D")
        ]
    (is (= a-date (-> (t/>> a-date 3 t/days-property)
                      (t/<< 3 t/days-property))))
    #_(is (= a-date (-> (t/>> a-date period)
                      (t/<< period))))))

(deftest prop-test
  (let [combos [[t/instant-deref [t/nanoseconds-property t/microseconds-property t/milliseconds-property
                                ; not days - mistakenly assumed to be 24hr in java.time
                                ] ;[t/seconds-property t/hours-property]
                 ]
                [t/zdt-deref [t/nanoseconds-property t/microseconds-property t/milliseconds-property
                            t/seconds-property t/hours-property
                            t/days-property ;t/months-property t/years-property
                            ]]
                [t/datetime-deref [t/nanoseconds-property t/microseconds-property t/milliseconds-property
                                 t/seconds-property t/hours-property
                                 t/days-property ;t/months-property t/years-property
                                 ]]
                [t/date-deref [t/days-property ;t/months-property t/years-property
                             ]]
                [t/yearmonth-deref [t/months-property t/years-property]]
                ;[t/monthday-deref [t/months-property t/days-property]]
                ]]
    (doseq [[now props xtras] combos
            shiftable-prop (concat props xtras)]
      (let [i-1 (now (t/clock-system-default-zone))
            i-2 (-> i-1
                    (t/>> 1 shiftable-prop))]
        (testing (str "shift until" i-1 " by " shiftable-prop)
          (is (= 1 (t/until i-1 i-2 shiftable-prop)))
          (is (= -1 (t/until i-2 i-1 shiftable-prop))))))
    (doseq [[now props] combos
            withable-prop props]
      (let [i-1 (now (t/clock-system-default-zone))
            current (t/get-field i-1 withable-prop)]
        (when-not (t/instant? i-1)
          (testing (str "with " i-1 " prop " (str withable-prop) " current " current
                     " " withable-prop)
            (is (not= i-1 (t/with i-1 (if (= 1 current) 2 1) withable-prop)) (str i-1 " " withable-prop))))))))


;(t/get-field (t/zdt-deref (t/clock-system-default-zone)) t/days-property)

(deftest clock-test
  (let [zone (t/timezone-deref (t/clock-system-default-zone))
        now (t/instant-deref (t/clock-system-default-zone))
        fixed (t/clock-fixed now zone)
        offset (t/clock-offset-millis fixed 1)
        zdt-atom (atom (t/zdt-parse "2024-02-22T00:00:00Z[Europe/London]"))
        clock-zdt-atom (t/clock-zdt-atom zdt-atom)]
    (is (= now (t/instant-deref fixed)))
    (is (= (t/>> now 1 t/milliseconds-property) (t/instant-deref offset)))
    (is (t/>= (t/instant-deref (t/clock-system-default-zone)) (t/instant-deref fixed)))
    (is (= (t/zdt->timezone (t/zdt-deref fixed)) (t/zdt->timezone (t/zdt-deref offset))))
    (is (= @zdt-atom (t/zdt-deref clock-zdt-atom)))
    (swap! zdt-atom t/>> 1 t/hours-property)
    (is (= @zdt-atom (t/zdt-deref clock-zdt-atom)))))

(comment
  (def now (t/instant-deref (t/clock-system-default-zone)))
  (def fixed (t/clock-fixed now "Europe/London"))
  
  )

(deftest adjust-test
  (testing "adjusting date"
    (is (= (t/date-parse "0001-01-01")
          (binding [t/*block-non-commutative-operations* false]
            (-> (t/date-deref (t/clock-system-default-zone))
                (t/with 1 t/days-property)
                (t/with 1 t/months-property)
                (t/with 1 t/years-property))))))
  #_(testing "adjusting instant"
      ; seems pointless and doesnt work in js as-is
      (let [i (-> (t/instant-deref (t/clock-system-default-zone))
                  (t/with 123 t/milliseconds-property)
                  (t/with 456 t/microseconds-property)
                  (t/with 789 t/nanoseconds-property))]
        (is (= 123 (t/get-field i t/milliseconds-property)))
        (is (= 456 (t/get-field i t/microseconds-property)))
        (is (= 789 (t/get-field i t/nanoseconds-property)))))
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
                                                    (-> (t/datetime-from {:year 1 :month t/month-january :day-of-month 1})
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
  (let [i (t/instant-parse "2020-02-02T09:19:42.123456789Z")]
    (is (-> (t/truncate i t/hours-property) ; fyi hours is biggest
            (t/instant+timezone "Europe/London")
            (t/zdt->minute)
            (zero?))))
  )

(deftest guardrails-test
  (is (thrown? #?(:clj Throwable :cljs js/Error) (t/>> (t/date-parse "2020-02-02") 1 t/years-property)))
  ;(is (thrown? #?(:clj Throwable :cljs js/Error) (t/>> (t/date-parse "2020-02-02") (d/period-parse "P1Y"))))
  (binding [t/*block-non-commutative-operations* false]
    (is (t/>> (t/date-parse "2020-02-02") 1 t/years-property))
    ;(is (t/>> (t/date-parse "2020-02-02") (d/period-parse "P1Y")))

    ))

(deftest comparison-test
  (doseq [{:keys [startf endf]} [
                                 {:startf #(t/instant-parse "2020-02-01T00:00:00Z") :endf #(t/instant-parse "2020-02-02T00:00:00Z")}
                                 {:startf #(t/zdt-parse "2020-02-01T00:00Z[Europe/London]") :endf #(t/zdt-parse "2020-02-02T00:00Z[Europe/London]")}
                                 {:startf #(t/datetime-parse "2020-02-01T00:00") :endf #(t/datetime-parse "2020-02-02T00:00")}
                                 {:startf #(t/date-parse "2020-02-01") :endf #(t/date-parse "2020-02-02")}
                                 {:startf #(t/yearmonth-parse "2020-02") :endf #(t/yearmonth-parse "2020-03")}
                                 {:startf (constantly (t/monthday-parse "--12-01")) :endf (constantly (t/monthday-parse "--12-02"))}]]
    (let [start (startf)
          end (endf)]
      (is (= end (t/max start end start end)))
      (is (= start (t/min start end start end)))

      (is (t/>= end end start))
      (is (not (t/>= start end end start)))

      (is (t/<= start end end))
      (is (not (t/<= end start end)))

      (is (t/> end start))
      (is (not (t/> start end)))

      (is (t/< start end))
      (is (not (t/< end start))))
    )

  )

(deftest eom-test
  (is (= (t/date-parse "2020-02-29") (t/yearmonth+day-at-end-of-month (t/yearmonth-parse "2020-02")))))

(deftest plus-test
  (let [clock (t/clock-system-default-zone)
        month-day (t/monthday-deref clock)
        year-month (t/yearmonth-deref clock)]
    (is (= month-day
          (-> month-day
              (t/monthday+year 2021)
              (t/date+time (t/time-deref clock))
              (t/datetime+timezone "Pacific/Honolulu")
              (t/zdt->monthday))))
    (is (= year-month
          (-> year-month
              (t/yearmonth+day-of-month 1)
              (t/date+time (t/time-deref clock))
              (t/datetime+timezone "Pacific/Honolulu")
              (t/zdt->yearmonth))))))

(deftest or-same-test
  (let [start (t/date-parse "2024-03-19")]
    (is (= start (t/date-next-or-same-weekday start 2)))
    (is (= (t/>> start 6 t/days-property) (t/date-next-or-same-weekday start 1)))
    (is (= start (t/date-prev-or-same-weekday start 2)))
    (is (= (t/<< start 1 t/days-property) (t/date-prev-or-same-weekday start 1)))))

;(remove-ns (.name *ns*))



