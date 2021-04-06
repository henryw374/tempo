(ns noodle.doodle
  (:require [com.widdindustries.tempo :as t]))

(def a-date (t/date-now))

;; move date forward 3 days
(def later (t/>> a-date (t/period-parse "P3D")))

(compare a-date a-date)
(compare a-date later)
(compare  later a-date)
(.-compare  (t/instant-parse "2021-04-06T14:56:39.023310Z"))

