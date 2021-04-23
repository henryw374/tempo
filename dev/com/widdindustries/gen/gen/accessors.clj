(ns com.widdindustries.gen.gen.accessors
  (:require [com.widdindustries.gen.graph :as graph]
            [com.widdindustries.gen.gen :as gen]
            [clojure.walk]
            [backtick]
            [clojure.set :as set]
            [camel-snake-kebab.core :as csk]
            [clojure.string :as string])
  (:import (java.time ZonedDateTime ZoneId LocalDate Instant LocalTime MonthDay LocalDateTime YearMonth)))

(def all-paths (let [paths (atom [])]
                 (clojure.walk/postwalk
                   (fn [node]
                     (when-let [pathz (:paths node)]
                       (swap! paths concat pathz)
                       )
                     node)
                   graph/with-paths)
                 @paths))

(defn ->sub-paths [path]
  (loop [x (count path)
         sub-paths #{path}]
    (if (zero? x)
      sub-paths
      (recur
        (dec x)
        (conj sub-paths (vec (take x path)))))))



(def full-paths
  (->>
    (loop [[next-path & remaining] all-paths
           access-paths #{}]
      (if-not next-path
        access-paths
        (recur remaining
          (set/union access-paths (->sub-paths next-path)))))
    (remove #(= 1 (count %)))
    (sort-by (juxt first second))))

(def kw->class 
  {
   'date      LocalDate
   'timezone  ZoneId
   'zdt       ZonedDateTime
   'instant   Instant
   'time      LocalTime
   'monthday  MonthDay
   'datetime  LocalDateTime
   'yearmonth YearMonth
   })

(defn upper-first [s]
  (str (string/upper-case
         (subs s 0 1)) (subs s 1 (count s))))

(defn java-accessor [path]
  (let [subject (first path)
        target  (last path)
        fn-name (symbol (str (name subject) "->" (name target)))]
    (backtick/template
      (defn ~fn-name [~(with-meta 'foo {:tag (get kw->class subject)})]
        (~(symbol
            (if-let [target-class (get kw->class target)]
              (str ".to" (.getSimpleName target-class))
              (str ".get" (upper-first (csk/->camelCaseString (name target)))))) foo)))))

(defn accessor-forms [feature]
  (->> full-paths
       (map (fn [path]
              (java-accessor path)
              ))))


(comment
  
  (binding [*print-meta* true]
    (pr (java-accessor [:zdt :date])))
  (defn zdt->date [^java.time.ZonedDateTime foo] (.toLocalDate foo))
  (zdt->date (ZonedDateTime/now))
  (->> (apply concat full-paths) set)
  

  
  ; problems
  ; getMonthValue
  ; ->month-day
  ; day-of-week getValue()
  
  
  )
