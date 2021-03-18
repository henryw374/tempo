(ns com.widdindustries.tempo.js-temporal-methods)

(defn from [^js x thing]
  (.from x thing))

(defn ->zdt-iso [^js x zone]
  (.toZonedDateTimeISO x zone))

(defn ->zdt-calendar [instant zone calendar]
  (-> ^js instant
      (.toZonedDateTime #js{:timeZone zone :calendar calendar})))

(defn ->plain-time [^js x]
  (.toPlainTime x))

(defn ->plain-date [^js x]
  (.toPlainDate x))

(defn ->plain-datetime [^js x]
  (.toPlainDateTime x))

(defn instant->plain-date-iso [instant zone]
  (-> ^js instant
      (->zdt-iso zone)
      (->plain-date)))

(defn instant->plain-date-calendar [instant zone calendar]
  (-> instant
      (->zdt-calendar zone calendar)
      (->plain-date)))

(defn instant->plain-datetime-iso [instant zone]
  (-> ^js instant
      (->zdt-iso zone)
      (->plain-datetime)))

(defn instant->plain-datetime-calendar [instant zone calendar]
  (-> instant
      (->zdt-calendar zone calendar)
      (->plain-datetime)))

(defn instant->plain-time-iso [instant zone]
  (-> ^js instant
      (->zdt-iso zone)
      (->plain-time)))

(defn tz-from [thing]
  (js/Temporal.TimeZone.from thing))
