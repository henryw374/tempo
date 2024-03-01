(ns com.widdindustries.tempo.clock
  (:refer-clojure :exclude [time])
  (:require [com.widdindustries.tempo.js-temporal-methods :as tm]))

(defn clock [instant-fn zone]
  #js{:instant          instant-fn
      :plainDateTime    (fn [calendar]
                          (tm/instant->plain-datetime-calendar (instant-fn) zone calendar)),
      :plainDateTimeISO (fn []
                          (tm/instant->plain-datetime-iso (instant-fn) zone))
      :plainDate        (fn [calendar]
                          (tm/instant->plain-date-calendar (instant-fn) zone calendar))
      :plainDateISO     (fn []
                          (tm/instant->plain-date-iso (instant-fn) zone))
      :plainTimeISO     (fn []
                          (tm/instant->plain-time-iso (instant-fn) zone))
      :timeZone         (fn [] (tm/tz-from zone))
      :zonedDateTimeISO (fn []
                          (tm/->zdt-iso (instant-fn) zone))})

(defn instant
  ([^js clock] (.instant clock)))

(defn datetime
  ([^js clock] (.plainDateTimeISO clock)))

(defn date
  ([^js clock] (.plainDateISO clock)))

(defn yearmonth
  ([^js clock] (js/Temporal.PlainYearMonth.from (date clock))))

(defn monthday
  ([^js clock] (js/Temporal.PlainMonthDay.from (date clock))))

(defn time
  ([^js clock] (.plainTimeISO clock)))

(defn timezone
  ([^js clock] (.timeZone clock)))

(defn zdt
  ([^js clock] (.zonedDateTimeISO clock)))