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


(def ^{:doc "each path is a list from root to leaf"}
  full-paths
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
   'date      'LocalDate
   ;'timezone  'ZoneId
   'zdt       'ZonedDateTime
   'instant   'Instant
   'time      'LocalTime
   'monthday  'MonthDay
   'datetime  'LocalDateTime
   'yearmonth 'YearMonth
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
  (cond
    (:xform-fn target) (:xform-fn target) 
    (:accessor target) (symbol (str "." (:accessor target)))
    :else nil))

(defn cljc-accessor [feature path]
  (let [subject (:tempo (first path))
        target (last path)
        target-name (:tempo target)
        fn-name (symbol (str (name subject) "->" (name target-name)))]
    (when-not (:ignore-accessor target)
      (backtick/template
        (defn ~fn-name [~(with-meta 'foo {:tag (get kw->class subject)})]
          (-> foo
              ~(symbol
              (if-let [x (special-accessor (or (get target :cljc) (get target :cljay)))]
                x
                (if-let [target-class (get kw->class target-name)]
                  (str (get kw->cljc-ns subject) "/to-" (csk/->kebab-case (.getSimpleName target-class)))
                  (str (get kw->cljc-ns subject) "/get-" (csk/->kebab-case (name target-name))))))))))))

(defn temporal-accessor [feature path]
  (let [subject (:tempo (first path))
        target (last path)
        target-name (:tempo target)
        fn-name (symbol (str (name subject) "->" (name target-name)))]
    #_(when (and (= 'zdt subject) (= 'timezone target-name))
      (sc.api/spy) (comment (eval `(sc.api/defsc ~(sc.api/last-ep-id)))))
    (when-not (:ignore-accessor target)
      (backtick/template
        (defn ~fn-name [~(with-meta 'foo {:tag (symbol (str "js/Temporal." (str (get kw->temporal-class subject))))})]
          (-> foo ~(if-let [x (special-accessor (get target feature))]
              x
              (symbol (if-let [target-class (get kw->temporal-class target-name)]
                        (str ".to" (str target-class))
                        (str ".-" (csk/->camelCaseString (name target-name))))))))))))

(comment
  (comment
    (def p (->> full-paths
                (filter (fn [path]
                          (and
                            (= 'zdt (:tempo (first path)))
                            ;(= 'timezone (:tempo (last path)))
                            ;(= 'timezone (:tempo (last path)))
                            )
                          ))
                (first)
                ))
    )
  (temporal-accessor :cljs p)
  (temporal-accessor :cljs [{:tempo 'zdt} {:tempo 'instant}])
  (temporal-accessor :cljs [{:tempo 'zdt} {:tempo 'hour}])
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
    (when-not (:ignore-accessor target)
      (backtick/template
        (defn ~fn-name [~(with-meta 'foo {:tag (get kw->class subject)})]
          (-> foo ~(if-let [x (special-accessor (get target feature))]
              x
              (symbol (if-let [target-class (get kw->class target-name)]
                        (str ".to" (str target-class))
                        (str ".get" (upper-first (csk/->camelCaseString (name target-name)))))))))))))

(defn accessor-forms [feature]
  ; problems
  ;ignore-accessor

  (->> full-paths
       (keep (fn [path]
               (case feature
                 :cljay (java-accessor feature path)
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

(comment
  (parse-fn :cljay (ffirst full-paths)))

(defn parse-forms [feature]
  (->> (apply concat (-> graph/with-paths keys) full-paths )
       distinct
       (keep (fn [thing]
               (parse-fn feature thing)))))

(defn now-fn [feature subject]
  (when (and (not (:no-now subject)) (get kw->class (:tempo subject)))
    (let [fn-name (str (:tempo subject) "-now")
          nower (case feature
                  :cljay (str (get kw->class (:tempo subject)) "/" (or (-> subject feature :parse) "now"))
                  :cljs (str "tempo-clock/" (:tempo subject)))]
      (backtick/template
        (defn ~(symbol fn-name)
          ([~(with-meta 'clock {:tag java.time.Clock})]
           (~(symbol nower) clock))))
      )))

(defn now-forms [feature]
  (->> (apply concat full-paths)
       distinct
       (keep (fn [thing]
               (now-fn feature thing)))))

(defn now-test [subject]
  (when (and (not (:no-now subject)) (get kw->class (:tempo subject)))
    (let [fn-name (str (:tempo subject) "-now-test")]
      (backtick/template
        (deftest ~(symbol fn-name)
          (let [now-now (~(symbol (str "t/" (:tempo subject) "-now")) (t/clock-system-default-zone))]
            (is (~(symbol (str "t/" (:tempo subject) "?")) now-now)))
          (let [clock-1 (t/clock-fixed (t/instant-parse "1955-11-01T16:46:08.017143Z") (str (t/timezone-now (t/clock-system-default-zone))))
                clock-2 (t/clock-fixed (t/instant-parse "1955-12-02T17:46:08.017143Z") (str (t/timezone-now (t/clock-system-default-zone))))
                now-clock-1 (~(symbol (str "t/" (:tempo subject) "-now")) clock-1)
                now-clock-2 (~(symbol (str "t/" (:tempo subject) "-now")) clock-2)
                ]
            (is (~(symbol (str "t/" (:tempo subject) "?")) now-clock-1))
            (is (t/> now-clock-2 now-clock-1))))))))

(defn now-tests []
  (->> (apply concat full-paths)
       distinct
       (keep (fn [thing]
               (now-test thing)))))

(defn accessor-test [path]
  (let [subject (:tempo (first path))
        target (last path)
        target-name (:tempo target)
        ;_ (when-not (get target :return) (sc.api/spy)) ;(sc.api/defsc [1 -1]) 
        ;_ (comment (eval `(sc.api/defsc ~(sc.api/last-ep-id))))
        fn-name (symbol (str (name subject) "->" (name target-name)))
        pred (or (get target :return)
               (symbol (str "t/" (name target-name) "?")))
        prop (symbol (str "t/" (name target-name) "s-property"))
        ]
    (when-not (:ignore-accessor target)
      ;(println (get target :return))
      (backtick/template
        (deftest ~(symbol fn-name)
          (let [now-now (~(symbol (str "t/" subject "-now")) (t/clock-system-default-zone))]
            (is (~pred (~(symbol (str "t/" fn-name)) now-now)))
            ~(when (and 
                     (and (not= 'monthday subject) (not= 'month target-name))
                     (not (contains? #{'day-of-week 'date 'day-of-month 'instant 'datetime 'time 'timezone 'legacydate
                                       'yearmonth 'monthday
                                          'epochmilli 'year 'month}
                               target-name)))
               (list 'is (list '= 'now-now (list 't/with 'now-now (list (symbol (str "t/" fn-name)) 'now-now) prop))))
            ))))))

(defn accessor-tests []
  (->> full-paths
       distinct
       (keep (fn [thing]
               (accessor-test thing)))))

(comment
  (->>
    full-paths
    distinct
    (filter #(not= 'day-of-week (-> % last :tempo)))
    ;(drop 2)
    (map accessor-test)
    (take 1)
    )

  (binding [*print-meta* true]
    (pr (java-accessor [:zdt :date])))
  (defn zdt->date [^java.time.ZonedDateTime foo] (.toLocalDate foo))
  (zdt->date (ZonedDateTime/now))
  (->> (apply concat full-paths) set)

  )

(defn parse-test [subject]
  (when (get kw->class (:tempo subject))
    (let [fn-name (str (:tempo subject) "-parse-test")]
      (if (= 'timezone (:tempo subject))
        (backtick/template
          (deftest ~(symbol fn-name)
            (is (= (t/timezone-now (t/clock-system-default-zone)) (-> (t/timezone-now (t/clock-system-default-zone)) str ~(symbol (str "t/" (:tempo subject) "-parse")))))))
        (backtick/template
          (deftest ~(symbol fn-name)
            (let [now-now (~(symbol (str "t/" (:tempo subject) "-now")) (t/clock-system-default-zone))]
              (is (= now-now (-> now-now str ~(symbol (str "t/" (:tempo subject) "-parse"))))))))))))

(defn parse-tests []
  (->> (apply concat full-paths)
       distinct
       (keep (fn [thing]
               (parse-test thing)))))
