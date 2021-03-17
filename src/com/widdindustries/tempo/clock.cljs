(ns com.widdindustries.tempo.clock)


(defprotocol Clock 
  )

(extend-protocol Clock js/Temporal.now)

(defrecord FixedClock [instant zone]
  Clock
  #js{:plainDateISO (fn [] (-> ^js instant
                               (.toZonedDateTimeISO zone)
                               (.toPlainDate)))})

(defn fixed-clock [instant zone]
  (FixedClock. instant zone))
