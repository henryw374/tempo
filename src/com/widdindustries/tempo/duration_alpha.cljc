(ns com.widdindustries.tempo.duration-alpha
  "significant differences exist between java.time and Temporal wrt amounts of time.
  
  For now, have separate ns.
 
  "
  #?(:clj (:import [java.time Duration Period])))

#?(:clj (set! *default-data-reader-fn* tagged-literal))

(defn duration-parse [p]
  #?(:cljs (js/Temporal.Duration.from p)
     :clj (. Duration parse p)))

(defn period-parse [p]
  #?(:cljs (js/Temporal.Duration.from p)
     :clj (. Period parse p)))

(defn duration->negated [d]
  #?(:cljs (.negated ^js d)
     :clj (.negated d))
  )

(comment

  (-> (duration-parse "PT2h30m")
      (.total #js{:unit "minute"})
      (.total #js{:unit "year"}))

  (-> (period-parse "P1Y")
      (.total #js{:unit "year" :relativeTo (com.widdindustries.tempo/zdt-now)}))
  )

;(defn duration->as-years ([d]) ([d ref]))
;(defn duration->as-months [d ref])
;(defn duration->as-days [d ref])
;(defn duration->as-hours [d])
;(defn duration->as-minutes [d])
;(defn duration->as-seconds [d])