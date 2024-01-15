(ns com.widdindustries.tempo.js-temporal-entities
  (:refer-clojure :exclude [time]))

(def instant js/Temporal.Instant)
(def zdt js/Temporal.ZonedDateTime)
(def date js/Temporal.PlainDate)
(def datetime js/Temporal.PlainDateTime)
(def time js/Temporal.PlainTime)
(def monthday js/Temporal.PlainMonthDay)
(def yearmonth js/Temporal.PlainYearMonth)
(def timezone js/Temporal.TimeZone)

;temporal-amounts
(def duration js/Temporal.Duration)
