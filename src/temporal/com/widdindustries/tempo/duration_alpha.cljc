(ns com.widdindustries.tempo.duration-alpha
  "significant differences exist between java.time and Temporal wrt amounts of time.
  
  For now, have separate ns.
  "
  #?(:clj (:import [java.time Duration Period])))

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

;(defn duration->as-years ([d]) ([d ref]))
;(defn duration->as-months [d ref])
;(defn duration->as-days [d ref])
;(defn duration->as-hours [d])
;(defn duration->as-minutes [d])
;(defn duration->as-seconds [d])