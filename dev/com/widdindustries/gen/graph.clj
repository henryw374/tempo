(ns com.widdindustries.gen.graph
  (:require [clojure.walk]
            [medley.core :as m])
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

(def temporal-types
  ['instant
   'zdt
   'date
   'datetime
   'time
   'monthday
   'yearmonth
   ])

;leaf entities are all numbers
; (zdt-from {args}) args either...
; instant
; timezone + date-time

(def epochmilli
  {:tempo 'epochmilli
   :cljay {:accessor 'toEpochMilli}})

(def epochnano
  {:ignore-accessor true ; its not in java
   :tempo 'epochnano})

(def timezone_id
  {:no-now true
   :cljay  {
            ;:accessor 'getZone
            :xform-fn '(-> (.getZone) (.getId))}
   :cljs   {:accessor '-timeZoneId}
   :tempo  'timezone_id})

(def timezone 
  {:tempo  'timezone
   :no-now true 
   :cljay {:parse    'of}})

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
   ;:ignore-accessor true
   :cljay    {;:ignore true 
              ;:accessor 'getDayOfWeek
              :xform-fn '(-> (.getDayOfWeek) (.getValue))
              }
   :cljs     {:accessor '-dayOfWeek
              ;:xform-fn '(-> (.-dayOfWeek) weekday-number->weekday)
              }})

(def graph
  {timezone {:parts {}}
   {:tempo 'instant} {:parts
                      {epochmilli   {}
                       epochnano    {}
                       {:tempo 'legacydate
                        :accessor 'getZone} {}}}
   {:tempo 'zdt
    ;todo - https://tc39.es/proposal-temporal/docs/zoneddatetime.html#startOfDay
    ;hours-in-day
    ;in-leap-year
    }                {:branches
                      [#_{:parts {
                                  {:tempo 'start-of-day} {}
                                  {:tempo 'hours-in-day} {}
                                  }}
                       {:parts {{:tempo 'instant} {}}}
                       {:parts {timezone_id {}
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
                                     {monthday {:parts {month                      {}
                                                        {:tempo 'day-of-month
                                                         :cljs  {:accessor '-day}} {}}}}}
                                    {:parts
                                     {{:tempo 'year}             {}
                                      month                      {}
                                      {:tempo 'day-of-month
                                       :cljs  {:accessor '-day}} {}}}
                                    {:parts
                                     {day-of-week {}}}]
                                   }
                                  {:tempo 'time} {:parts {{:tempo 'hour}   {}
                                                          {:tempo 'minute} {}
                                                          {:tempo 'second} {}
                                                          {:tempo 'nano}   {}}}}}}}]}})

(def with-paths (paths graph))

(comment 
  (-> with-paths 
       keys)
  
  (->> with-paths
       (m/find-first #(= {:tempo 'timezone} (select-keys (key %1) [:tempo]))))
  )