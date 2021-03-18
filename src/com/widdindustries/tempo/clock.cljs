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
