(ns com.widdindustries.tempo.tempo-clock
  (:refer-clojure :exclude [time])
  (:require [com.widdindustries.tempo.js-temporal-methods :as tm]))

(defn clock [instant-fn timezone_id-fn]
  #js{:instant          instant-fn
      :plainDateTimeISO (fn []
                          (tm/instant->plain-datetime-iso (instant-fn) (timezone_id-fn)))
      :plainDateISO     (fn []
                          (tm/instant->plain-date-iso (instant-fn) (timezone_id-fn)))
      :plainTimeISO     (fn []
                          (tm/instant->plain-time-iso (instant-fn) (timezone_id-fn)))
      :timeZoneId       (fn [] (timezone_id-fn))
      :zonedDateTimeISO (fn []
                          (tm/->zdt-iso (instant-fn) (timezone_id-fn)))})

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

(defn timezone_id
  ([^js clock] (.timeZoneId clock)))

(defn zdt
  ([^js clock] (.zonedDateTimeISO clock)))