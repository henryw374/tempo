(ns noodle.doodle
  (:require [com.widdindustries.tempo :as t]))

(def a-date (t/date-now))

;; move date forward 3 days
(t/>> a-date (t/period-parse "P3D"))
