(ns com.widdindustries.gen.graph
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
   'time-zone])

;leaf entities are all numbers
; (zdt-from {args}) args either...
; instant
; timezone + date-time

(def graph
  {
   :instant {:graph {:epochmillis {}
                     :epochnanos  {}}}
   :zdt
            {:graph       [{:instant {}}
                           {:timezone {}
                            :datetime {:graph {
                                               :date {:graph    [{:yearmonth {:graph {:year  {}
                                                                                      :month {}}
                                                                              :java  {:no-getter true}}
                                                                  :day       {}}
                                                                 {:monthday {:java  {:no-getter true}
                                                                             :graph {:month {}
                                                                                     :day   {}}}
                                                                  :year     {}}
                                                                 {:year  {}
                                                                  :month {}
                                                                  :day   {}}
                                                                 ]
                                                      :get-only {:day-of-week {}}}
                                               :time {:graph {:hours   {}
                                                              :minutes {:seconds      {}
                                                                        :milliseconds {}
                                                                        :microseconds {}
                                                                        :nanoseconds  {}}}}
                                               }}}]}}
  )