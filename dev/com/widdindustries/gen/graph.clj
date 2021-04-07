(ns com.widdindustries.gen.graph
  (:require [clojure.walk]
            [sc.api])
  (:import (java.time ZonedDateTime)))

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
  {:instant {:parts
             {:epochmillis {}
              :epochnanos  {}}}
   :zdt     {:branches
             [{:parts {:instant
                       {}}}
              {:parts {:timezone
                       {}
                       :datetime
                       {:branches
                        [{:parts
                          {:date
                                 {:branches
                                            [{:parts
                                              {:yearmonth {:parts {:year  {}
                                                                   :month {}}
                                                           :java  {:no-getter true}}
                                               :day       {}}}
                                             {:parts
                                              {:monthday {:java  {:no-getter true}
                                                          :parts {:month {}
                                                                  :day   {}}}
                                               :year     {}}}
                                             {:parts
                                              {:year  {}
                                               :month {}
                                               :day   {}}}]
                                  :get-only {:day-of-week {}}}
                           :time {:parts {:hours        {}
                                          :minutes      {}
                                          :seconds      {}
                                          :milliseconds {}
                                          :microseconds {}
                                          :nanoseconds  {}}}}}]}}}]}})


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

(comment


  (paths {:graph [{:time {:graph [{:single {:hours {}}}]}}]})
  (paths {:single {:time {:single {:hours {}}}}})
  (paths {:single {:time {:graph [{:hours {}}]}}})


  (-> (paths graph)
      ;(get-in [:graph :zdt :paths])
      ;(get-in [:graph :zdt :graph 1 :datetime :paths])
      )

  )

(comment

  (->>
    {:time {:branches [{:parts {:volons  {}
                                :mumbles {}}}
                       {:parts {:hours   {}
                                :minutes {:parts {:seconds {}}}}}]}}
    )

  )