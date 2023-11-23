(ns com.widdindustries.demo
  (:require [com.widdindustries.tempo :as t]
    ;[com.widdindustries.tempo.cljs-protocols :as cljs-protocols]
    ;       [com.widdindustries.tempo.js-temporal-entities :as entities]
    ;       [com.widdindustries.tempo.js-temporal-methods :as methods]
    ;        [com.widdindustries.tempo.clock :as clock]
            ))

(comment
  
  (t/date-now)
  ;(t/date-now clock)
  (t/date-parse "2020-02-02") ;iso strings only
  ;(t/date-from {:year 2020 :month 2 :day-of-month 2})
  ; the -from functions accept a map of components which is sufficient to build the entity
  (t/datetime-from {:date (t/date-parse "2020-02-02") :time (t/time-now)})
  ; or equivalently
  (t/datetime-from {:year 2020 :month 2 :day 2 :time (t/time-now)})
  ; with -from, you can use smaller or larger components. 
  ; larger ones take precedence. below, the :year is ignored, because the :date took precedence (being larger) 
  (t/datetime-from {:year 2021 :date (t/date-parse "2020-02-02") :time (t/time-now)})

  ; to get parts of an entity, start with the subject and add ->
  ;(t/date->yearmonth (t/date-now))
  (t/date->month (t/date-now))
  (t/zdt->nano (t/zdt-now))
  ;(-> (t/instant-now) (t/instant->epochmillis))


  ;(t/period->days (t/period-parse "P3Y5M3D")) ; > 3

  ;(t/duration->as-minutes (t/duration-parse "PT3H")) ; > 180

  ; following won't exist bc years and months are variable length
  ;(t/period->as-days (t/period-parse "P3Y5M3D"))


  ;(t/+ (t/duration-parse "PT3H") (t/duration-parse "PT3S"))


  ;; move date forward 3 days
  ;(t/>> (t/date-now)  (t/period-parse "P3D"))

  ;(-> (t/date-now) (t/with {:year 2021 :month 7}))
  ;(-> (t/date-now) (t/with-year 3030))

  ; todo - is this easily doable with platform api??
  ;(-> (t/date-now) (t/truncate-to-month))
  ;(-> (t/instant-now) (t/truncate-to-month))


  ;only entities of the same type can be compared

  (t/>= (t/date-parse "2020-05-05") (t/date-parse "2021-05-05"))


  (t/max (t/date-parse "2020-05-05") (t/date-parse "2021-05-05") (t/date-parse "1920-05-05"))

  ; you must specify unit
  ;(t/until a b :minutes)



          )
