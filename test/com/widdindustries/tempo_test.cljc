(ns com.widdindustries.tempo-test
  (:require [clojure.test :refer [deftest is testing]]
            [com.widdindustries.tempo :as t]
            [com.widdindustries.tempo.duration-alpha :as d]))

 (t/extend-all-cljs-protocols)

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
  (let [a-date (t/date-now)
        period (d/period-parse "P3D")
        plus3 (t/>> a-date period)]
    (is (= a-date (t/<< plus3 period)))))


