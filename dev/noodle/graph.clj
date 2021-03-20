(ns noodle.graph)

;leaf entities are all numbers
; (zdt-from {args}) args either...
; instant
; timezone + date-time

(def graph
  {
   :instant {:graph {:epochmillis {}
                     :epochnanos  {}}}
   :zdt
            {:graph [{:instant   {}}
                     {:timezone {}
                      :datetime {:graph {
                                          :date {:graph [{:yearmonth {:graph {:year  {}
                                                                               :month {}}
                                                                       :java  {:no-getter true}}
                                                          :day        {}}
                                                         {:monthday {:java  {:no-getter true}
                                                                      :graph {:month {}
                                                                              :day   {}}}
                                                          :year      {}}
                                                         {:year  {}
                                                          :month {}
                                                          :day   {}}
                                                         ]}
                                          :time {:graph {:hours   {}
                                                         :minutes {:seconds      {}
                                                                   :milliseconds {}
                                                                   :microseconds {}
                                                                   :nanoseconds  {}}}}
                                          }}}]}}
  )