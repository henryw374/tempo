(ns com.widdindustries.tempo.clock
  (:refer-clojure :exclude [time])
  (:require [com.widdindustries.tempo.js-temporal-methods :as tm]))

(defn fixed-clock [instant zone]
  #js{:instant          (fn [] instant)
      :plainDateTime    (fn [calendar]
                          (tm/instant->plain-datetime-calendar instant zone calendar)),
      :plainDateTimeISO (fn []
                          (tm/instant->plain-datetime-iso instant zone))
      :plainDate        (fn [calendar]
                          (tm/instant->plain-date-calendar instant zone calendar))
      :plainDateISO     (fn []
                          (tm/instant->plain-date-iso instant zone))
      :plainTimeISO     (fn []
                          (tm/instant->plain-time-iso instant zone))
      :timeZone         (fn [] (tm/tz-from zone))
      
      :zonedDateTimeISO (fn []
                          (tm/->zdt-iso instant zone))}
  )


(defn instant 
  ([] (js/Temporal.Now.instant))
  ([^js clock] (.instant clock)))

(defn datetime 
  ([] (js/Temporal.Now.plainDateTimeISO))
  ([^js clock] (.plainDateTimeISO clock)))

(defn date 
  ([] (js/Temporal.Now.plainDateISO))
  ([^js clock] (.plainDateISO clock)))

(defn yearmonth
  ([] (js/Temporal.PlainYearMonth.from (date)) )
  ([^js clock] (js/Temporal.PlainYearMonth.from (date clock))))

(defn monthday
  ([] (js/Temporal.MonthDay.from (date)) )
  ([^js clock] (js/Temporal.MonthDay.from (date clock))))

(defn time 
  ([] (js/Temporal.Now.plainTimeISO))
  ([^js clock] (.plainTimeISO clock)))

(defn time-zone 
  ([] (js/Temporal.Now.timeZone))
  ([^js clock] (.timeZone clock)))

#_(defn zoned-date-time 
  ([^js calendar] (js/Temporal.Now.zonedDateTime calendar))
  ([^js clock ^js calendar] (.zonedDateTime clock calendar)))

(defn zdt 
  ([] (js/Temporal.Now.zonedDateTimeISO))
  ([^js clock] (.zonedDateTimeISO clock)))