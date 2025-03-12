(ns com.widdindustries.gen.graph
  (:require [clojure.walk]
    ;[com.widdindustries.tempo :as t]
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
   :return 'int?
   :cljay {:accessor 'toEpochMilli}
   :cljs  {:xform-fn '(-> (.-epochMilliseconds))}})

(def epochnano
  {:ignore-accessor true ; its not in java
   :return 'int?
   :tempo 'epochnano})

(def timezone_id
  {:no-now true
   :return 'string?
   :cljay  {
            ;:accessor 'getZone
            :xform-fn '(-> (.getZone) (.getId))}
   :cljs   {:accessor '-timeZoneId}
   :tempo  'timezone_id})

#_(def timezone 
  {:tempo  'timezone
   :no-now true 
   :cljay {:parse    'of}})

(def yearmonth
  {:needed-to-go-up {'day-of-month {}}
   :tempo           'yearmonth
   ;:ignore-accessor true
   :cljs  {          :xform-fn '(-> (js/Temporal.PlainYearMonth.from))}
   :cljay           {;:no-getter true
                     :xform-fn '(-> (YearMonth/from))
                     }})

(def month
  {:tempo 'month
   :return 'int?
   :cljay {:accessor 'getMonthValue}})

(def monthday
  {:tempo           'monthday
   ;:ignore-accessor true
   ;:needed-to-go-up {'year {}}
   :cljs  {          :xform-fn '(-> (js/Temporal.PlainMonthDay.from))}
   :cljay           {;:no-getter true
                     :xform-fn '(-> (MonthDay/from))
                     }    })

(def day-of-week
  {:tempo    'day-of-week
   :return 'int?
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
  { ;timezone          {:parts {}}
   {:tempo 'instant} {:parts
                      {epochmilli  {}
                       epochnano   {}
                       {:tempo 'legacydate
                        :cljay {:xform-fn '(-> (.toEpochMilli) (java.util.Date.))}
                        :cljs  {
                                :xform-fn '(-> (.-epochMilliseconds) (js/Date.))
                                }} {}}}
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
                                {:tempo 'datetime}
                                {:parts
                                 {{:tempo 'date}
                                  {:branches
                                   [{:parts
                                     {yearmonth {:parts {{:tempo  'year
                                                          :return 'int?} {}
                                                         month           {}}}}}
                                    {:parts
                                     {monthday {:parts {{:tempo  'month
                                                         :return 'int?
                                                         ;get month not gener
                                                         :cljay {:accessor 'getMonthValue}
                                                         :cljs   {:xform-fn '(-> (.-monthCode) (subs 1 3) js/parseInt)}} {}
                                                        {:tempo  'day-of-month
                                                         :return 'int?
                                                         :cljs   {:accessor '-day}}                                      {}}}}}
                                    {:parts
                                     {{:tempo  'year
                                       :return 'int?}             {}
                                      month                       {}
                                      {:tempo  'day-of-month
                                       :return 'int?
                                       :cljs   {:accessor '-day}} {}}}
                                    {:parts
                                     {day-of-week {}}}]
                                   }
                                  {:tempo 'time} {:parts {{:tempo  'hour
                                                           :return 'int?}                          {}
                                                          {:tempo  'minute
                                                           :return 'int?}                          {}
                                                          {:tempo  'second
                                                           :return 'int?}                          {}
                                                          {:tempo  'millisecond
                                                           :return 'int?
                                                           :cljay  {:xform-fn '(-> -millisecond)}} {}
                                                          {:tempo  'microsecond
                                                           :return 'int?
                                                           :cljay  {:xform-fn '(-> -microsecond)}} {}
                                                          {:tempo  'nanosecond
                                                           :return 'int?
                                                           :cljay  {:xform-fn '(-> -nanosecond)}}  {}}}}}}}]}})

(def with-paths (paths graph))

(comment 
  (-> with-paths 
       keys)
  
  (->> with-paths
       (m/find-first #(= {:tempo 'timezone} (select-keys (key %1) [:tempo]))))
  )