(ns com.widdindustries.gen.graph
  (:require [clojure.walk]
            [sc.api])
  (:import (java.time ZonedDateTime)))


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

(def graph
  {'instant {:parts
             {'epochmillis {}
              'epochnanos  {}}}
   'zdt     {:branches
             [{:parts {'instant {}}}
              {:parts {'timezone {}
                       'datetime
                        {:parts
                             {'date
                                    {:branches
                                               [{:parts
                                                 ^{:needed-to-go-up {'day-of-month {}}
                                                   :java            {:no-getter true}}
                                                 {'yearmonth {'parts {'year  {}
                                                                      'month {}}}}}
                                                {:parts
                                                 ^{:needed-to-go-up {'year {}}
                                                   :java  {'no-getter true}}
                                                 {'monthday {:parts {'month {}
                                                                     'day-of-month   {}}}}}
                                                {:parts
                                                 {'year  {}
                                                  ^{:java {:accessor 'getMonthValue}}
                                                  'month {}
                                                  'day-of-month   {}}}
                                                {:parts
                                                 ^{:get-only true}
                                                 {'day-of-week {}}}]
                                      }
                              'time {:parts {'hour   {}
                                             'minute {}
                                             'second {}
                                             'nano   {}}}}}}}]}})

(def with-paths (paths graph))

