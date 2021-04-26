(ns com.widdindustries.gen.graph
  (:require [clojure.walk]
            [sc.api])
  (:import (java.time ZonedDateTime MonthDay DayOfWeek YearMonth)))


(defn parts->paths [parent-key parts]
  (->> parts
       (mapcat (fn [[k v]]
                 (or
                   (:paths v)
                   (list (list k)))))
       (map (fn [path]
              (cons parent-key path)))))

(defn paths [graph]
  (clojure.walk/postwalk
    (fn [node]
      (cond
        (and (map-entry? node) (-> node val :branches))
        [(key node)
         (assoc (val node) :paths
                           (->> (-> node val :branches)
                                (mapcat (fn [x]
                                          (parts->paths (key node) (:parts x))))))]
        (and (map-entry? node) (-> node val :parts))
        [(key node)
         (assoc (val node) :paths
                           (parts->paths (key node)
                             (-> node val :parts)))]
        :else node))
    graph))


(def non-graph {:zdt {:js-temporal {:entity-name 'ZonedDateTime}}})

(def temporal-types
  ['instant
   'zdt
   'date
   'datetime
   'time
   'month-day
   'year-month
   ])

(def non-temporal-types
  ['time-zone])

;leaf entities are all numbers
; (zdt-from {args}) args either...
; instant
; timezone + date-time

(def epochmilli
  {:tempo 'epochmilli
   :cljay {:accessor 'toEpochMilli}})

(def epochnano
  {:ignore-accessor true ; not in java
   :tempo 'epochnano})

(def timezone
  {:no-now true
   :cljay {:parse 'of
           :accessor 'getZone}
   :cljc {:parse 'of
          :accessor 'getZone}
   :tempo 'timezone})

(def yearmonth
  {:needed-to-go-up {'day-of-month {}}
   :tempo           'yearmonth
   :ignore-accessor true
   :cljay           {:no-getter true
                     :fn-args   ['year 'month]
                     :fn        (fn [year month]
                                  (YearMonth/of ^int year ^int month))}})

(def month
  {:tempo 'month
   :cljay {:accessor 'getMonthValue}})

(def monthday
  {:tempo           'monthday
   :ignore-accessor true
   :needed-to-go-up {'year {}}
   :cljay           {:no-getter true
                     :fn-args   ['month 'day-of-month]
                     :fn        (fn [month day]
                                  (MonthDay/of ^int month ^int day))}})

(def day-of-week
  {:tempo    'day-of-week
   :get-only true
   :ignore-accessor true
   :cljay    {:ignore true 
              :accessor {:wrap '(fn [^DayOfWeek dow] (.getValue dow))}}})

(def graph
  {{:tempo 'instant} {:parts
                      {epochmilli {}
                       epochnano  {}}}
   {:tempo 'zdt
    ;todo - https://tc39.es/proposal-temporal/docs/zoneddatetime.html#startOfDay
    ;hours-in-day
    ;in-leap-year
    }     {:branches
                      [{:parts {{:tempo 'instant} {}}}
                       {:parts {timezone {}
                                {:tempo 'datetime
                                 ;todo - in leap year? daysinyear?
                                 }
                                         {:parts
                                          {{:tempo 'date}
                                                          {:branches
                                                           [{:parts
                                                             {yearmonth {:parts {{:tempo 'year} {}
                                                                                 month          {}}}}}
                                                            {:parts
                                                             {monthday {:parts {month                  {}
                                                                                {:tempo 'day-of-month} {}}}}}
                                                            {:parts
                                                             {{:tempo 'year}         {}
                                                              month                  {}
                                                              {:tempo 'day-of-month} {}}}
                                                            {:parts
                                                             {day-of-week {}}}]
                                                           }
                                           {:tempo 'time} {:parts {{:tempo 'hour}   {}
                                                                   {:tempo 'minute} {}
                                                                   {:tempo 'second} {}
                                                                   {:tempo 'nano}   {}}}}}}}]}})

(def with-paths (paths graph))

