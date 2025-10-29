(ns com.widdindustries.chronos.chronos-clock
  (:refer-clojure :exclude [time]))

(defn from [^js x thing]
  (.from x thing))

(defn ->zdt-iso [^js x zone]
  (.toZonedDateTimeISO x zone))

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

(defn instant->plain-datetime-iso [instant zone]
  (-> ^js instant
      (->zdt-iso zone)
      (->plain-datetime)))

(defn instant->plain-time-iso [instant zone]
  (-> ^js instant
      (->zdt-iso zone)
      (->plain-time)))


(defn clock [instant-fn timezone-fn]
  #js{:instant          instant-fn
      :plainDateTimeISO (fn []
                          (instant->plain-datetime-iso (instant-fn) (timezone-fn)))
      :plainDateISO     (fn []
                          (instant->plain-date-iso (instant-fn) (timezone-fn)))
      :plainTimeISO     (fn []
                          (instant->plain-time-iso (instant-fn) (timezone-fn)))
      :timeZoneId       (fn [] (timezone-fn))
      :zonedDateTimeISO (fn []
                          (->zdt-iso (instant-fn) (timezone-fn)))})

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
  ([^js clock] (.timeZoneId clock)))

(defn zdt
  ([^js clock] (.zonedDateTimeISO clock)))