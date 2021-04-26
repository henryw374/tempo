(ns com.widdindustries.tempo.clock
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
      :zonedDateTime    (fn [calendar]
                          (tm/->zdt-calendar instant zone calendar))
      :zonedDateTimeISO (fn []
                          (tm/->zdt-iso instant zone))}
  )


(defn instant 
  ([] (js/Temporal.now.instant))
  ([^js clock] (.instant clock)))
#_(defn plain-datetime 
  ([^js calendar] (js/Temporal.now.plainDateTime calendar))
  ([^js clock ^js calendar] (.plainDateTime clock calendar)))
(defn plain-datetime-iso 
  ([] (js/Temporal.now.plainDateTimeISO))
  ([^js clock] (.plainDateTimeISO clock)))
#_(defn plain-date 
  ([^js calendar] (js/Temporal.now.plainDate calendar))
  ([^js clock ^js calendar] (.plainDate clock calendar)))
(defn plain-date-iso 
  ([] (js/Temporal.now.plainDateISO))
  ([^js clock] (.plainDateISO clock)))
(defn plain-time-iso 
  ([] (js/Temporal.now.plainTimeISO))
  ([^js clock] (.plainTimeISO clock)))
(defn time-zone 
  ([] (js/Temporal.now.timeZone))
  ([^js clock] (.timeZone clock)))
#_(defn zoned-date-time 
  ([^js calendar] (js/Temporal.now.zonedDateTime calendar))
  ([^js clock ^js calendar] (.zonedDateTime clock calendar)))
(defn zoned-date-time-iso 
  ([] (js/Temporal.now.zonedDateTimeISO))
  ([^js clock] (.zonedDateTimeISO clock)))