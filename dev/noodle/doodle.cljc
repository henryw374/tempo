(ns noodle.doodle
  (:require [com.widdindustries.tempo :as t]))

(def a-date (t/now-date))

;; move date forward 3 days
(t/>> a-date (t/parse-period "P3D"))
