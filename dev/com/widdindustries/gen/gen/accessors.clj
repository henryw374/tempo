(ns com.widdindustries.gen.gen.accessors
  (:require [com.widdindustries.gen.graph :as graph]
            [com.widdindustries.gen.gen :as gen]
            [clojure.walk]
            [backtick]
            [medley.core :as m]
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
    (m/distinct-by (fn [path] [(-> path first :tempo) (-> path last :tempo)]))
    (sort-by (juxt (comp first :tempo) (comp second :tempo)))))

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

(def kw->cljc-ns
  {
   'date      'cljc.java-time.local-date
   'timezone  'cljc.java-time.zone-id
   'zdt       'cljc.java-time.zoned-date-time
   'instant   'cljc.java-time.instant
   'time      'cljc.java-time.local-time
   'monthday  'cljc.java-time.month-day
   'datetime  'cljc.java-time.local-date-time
   'yearmonth 'cljc.java-time.year-month
   })

(def kw->temporal-class 
  {
   'date      'PlainDate
   'timezone  'TimeZone
   'zdt       'ZonedDateTime
   'instant   'Instant
   'time      'PlainTime
   'monthday  'PlainMonthDay
   'datetime  'PlainDateTime
   'yearmonth 'PlainYearMonth
   })

(defn upper-first [s]
  (str (string/upper-case
         (subs s 0 1)) (subs s 1 (count s))))

(defn special-accessor [target]
  (cond (:accessor target) (str "." (:accessor target))
        :else nil))

(defn cljc-accessor [feature path]
  (let [subject (:tempo (first path))
        target  (last path)
        target-name (:tempo target)
        fn-name (symbol (str (name subject) "->" (name target-name)))]
    (when-not (:ignore-accessor target)
      (backtick/template
        (defn ~fn-name [~(with-meta 'foo {:tag (get kw->class subject)})]
          (~(symbol
              (if-let [x (special-accessor (or (get target :cljc) (get target :cljay)))]
                x
                (if-let [target-class (get kw->class target-name)]
                  (str (get kw->cljc-ns subject) "/to-" (csk/->kebab-case (.getSimpleName target-class)))
                  (str (get kw->cljc-ns subject) "/get-" (csk/->kebab-case (name target-name))))))
            foo))))))

(defn temporal-accessor [feature path]
  (let [subject (:tempo (first path))
        target (last path)
        target-name (:tempo target)
        fn-name (symbol (str (name subject) "->" (name target-name)))]
    ;(when-not (:ignore-accessor target))
    (backtick/template
      (defn ~fn-name [~(with-meta 'foo {:tag (get kw->class subject)})]
        )))
  )

(defn java-accessor [feature path]
  (let [subject (:tempo (first path))
        target (last path)
        target-name (:tempo target)
        ;         _  (sc.api/spy)
        ;(sc.api/defsc [1 -1]) 
        ;_ (comment (eval `(sc.api/defsc ~(sc.api/last-ep-id))))
        fn-name (symbol (str (name subject) "->" (name target-name)))
        ]
    (when-not (:ignore-accessor  target)
      (backtick/template
        (defn ~fn-name [~(with-meta 'foo {:tag (get kw->class subject)})]
          (~(symbol
              (if-let [x (special-accessor (get target feature))]
                x
                (if-let [target-class (get kw->class target-name)]
                  (str ".to" (.getSimpleName target-class))
                  (str ".get" (upper-first (csk/->camelCaseString (name target-name))))))) foo))))))

(defn accessor-forms [feature]
  ; problems
  ;ignore-accessor
  
  (->> full-paths
       (keep (fn [path]
              (case feature
                :cljay (java-accessor feature path)
                :cljc (cljc-accessor feature path)
                ;todo
                :cljs (temporal-accessor feature path))
              ))))

(defn parse-fn [feature subject]
  (when (get kw->class (:tempo subject))
    (let [fn-name (str (:tempo subject) "-parse")
          parser (case feature
                   :cljay (str (get kw->class (:tempo subject)) "/" (or (-> subject feature :parse) "parse"))
                   :cljc (str (get kw->cljc-ns (:tempo subject)) "/" (or (-> subject feature :parse) "parse"))
                   :cljs (str "js/Temporal." (get kw->temporal-class (:tempo subject)) ".from")
                   )]
      
      (backtick/template
        (defn ~(symbol fn-name) [~(with-meta 'foo {:tag String})]
          (~(symbol parser) foo)))
      )))

(defn parse-forms [feature]
  (->> (apply concat full-paths)
       distinct
       (keep (fn [thing]
               (parse-fn feature thing))))


;  (defn duration-parse [s]
;    #?(:clj (Duration/parse s)
;       :cljs (methods/from entities/duration s)))
;
;  (defn period-parse [s]
;    #?(:clj (Period/parse s)
;       :cljs (duration-parse s)))
;
  )

(defn now-fn [feature subject]
  (when (and (not (:no-now subject)) (get kw->class (:tempo subject)))
    (let [fn-name (str (:tempo subject) "-now")
          nower (case feature
                   :cljay (str (get kw->class (:tempo subject)) "/" (or (-> subject feature :parse) "now"))
                   :cljs (str "clock/"(:tempo subject) )
                   :cljc (str (get kw->cljc-ns (:tempo subject)) "/" (or (-> subject feature :parse) "now")))]
      ;; construction from clocks
;      (defn date-now
;        ([] #?(:clj (LocalDate/now)
;               :cljs (clock/plain-date-iso)))
;        ([clock]
;         #?(:clj (LocalDate/now ^Clock clock)
;            :cljs (clock/plain-date-iso clock))))
;
      
      (backtick/template
        (defn ~(symbol fn-name)
          ([] (~(symbol nower)))
          ([~(with-meta 'clock {:tag java.time.Clock})]
           (~(symbol nower) clock))))
      )))

(defn now-forms [feature]
  (->> (apply concat full-paths)
       distinct
       (keep (fn [thing]
               (now-fn feature thing)))))


(comment
  
  (binding [*print-meta* true]
    (pr (java-accessor [:zdt :date])))
  (defn zdt->date [^java.time.ZonedDateTime foo] (.toLocalDate foo))
  (zdt->date (ZonedDateTime/now))
  (->> (apply concat full-paths) set)
  
  )
