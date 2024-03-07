(ns com.widdindustries.tempo.tick
  "A partial port of tick.core, but now backed by Temporal on js runtime.
  
  The reason that is has been ported is that it is a popular date/time library. As well as having a different basis on JS runtimes,
  there are some philosophical differences between tick and tempo. Some people may prefer the tick approach and some
  (myself included) may prefer the tempo approach. 
  
  What is there to choose between tempo.tick and tempo.core?
  
  1. tempo.core has no zero-arg 'now' functions. Similarly, there is no implicit use of
  the 'current' or 'ambient' zone. For this reason, there is no `with-clock` macro in tempo.core
  
  2. tempo.core is sometimes more verbose, but has less 'magic' - for example to get the year of an Instant for example first requires converting to a zdt by explicitly supplying a zone.
  
  3. tempo.core distinguishes between parsing strings and accessing properties. For example in tick `(t/date x)` will parse x if it is a string,
  or access the date from x if it is e.g. a zdt
  
  4. tempo.core blocks non-commutative operations by default
  
  5. tempo.tick has functions which return temporal-amount objects.     
  
  Where some function (or a particular arity of a function) doesnt gel with Tempo, it has not implemented.
  
  There are no functions which have the same name+arity as juxt/tick but which work differently
  
  Major differences:
  
  unlike tick, no entity for year or month - where needed, numbers are used to repsesent those
  
  entities for temporal-amounts are alpha - so not included here yet
  
  no parsing or formatting of non-ISO strings - since this is not in Temporal
  
  Although there is a timezone entity, functions that would accept or return a timezone instead accept or return a string
  
  There is no offset-datetime
  
  "
  (:refer-clojure :exclude [min-key max-key format + - inc dec max min range time int long = < <= > >= next >> << atom swap! swap-vals! compare-and-set! reset! reset-vals! second divide])
  (:require [com.widdindustries.tempo :as t])
  #?(:clj (:import (java.time Instant))))

(def ^{:dynamic true} *clock* (t/clock-system-default-zone))

(defn now "same as (t/instant)" ([]
                                 (t/instant-now *clock*)))

(defn today "same as (t/date)" ([]
                                (t/date-now *clock*)))

(defn epoch "Constant for the 1970-01-01T00:00:00Z epoch instant" ([]
                                                                   #?(:clj Instant/EPOCH
                                                                      :cljs (js/Temporal.Instant.from 0))))

; not implementing as not really important
;(defn midnight "" ([]) ([date]))
;(defn noon "" ([]) ([date]))

(defn new-time "" ([]
                   (t/time-now *clock*)) 
  ([hour minute]
   (t/time-from {:hour   hour
                 :minute minute})) 
  ([hour minute second]
   (t/time-from 
     {:hour   hour
      :minute minute
      :second second}))
  ([hour minute second fraction]
   ;todo
   (let [[millisecond microsecond nanosecond] fraction]
     (t/time-from {:hour   hour
                   :minute minute
                   :second second
                   :millisecond millisecond 
                   :microsecond microsecond 
                   :nanosecond nanosecond }))))

(defn new-date "" ([]
                   (t/date-now *clock*))
  ([year month day-of-month]
   (t/date-from {:year year :month month :day-of-month day-of-month}))
  ;([year day-of-year]) ([epoch-day])
  )

(defn new-year-month "" ([]
                         (t/yearmonth-now *clock*)) 
  ([year month]
   (t/yearmonth-from {:year year :month month})))

(defn current-zone "Return the current zone, which can be overridden by the *clock* dynamic var" 
  ([]
   (t/timezone-now *clock*)))

;(defn zone-offset "" ([offset]) ([hours minutes]) ([hours minutes seconds]))

(def ^{:private true} unit-map
  {:nanos     t/nanoseconds-property
   :micros    t/microseconds-property
   :millis    t/milliseconds-property
   :seconds   t/seconds-property
   :minutes   t/minutes-property
   :hours     t/hours-property
   :days      t/days-property
   :months    t/months-property
   :years     t/years-property })

(defn with "Adjust a temporal with an adjuster or field" 
  ([t fld new-value]
     (t/with t new-value (get unit-map fld))))

;don't port
;(defn day-of-week-in-month "" ([ordinal day-of-week]) ([t ordinal day-of-week]))
;(defn first-day-of-month "" ([]) ([t]))
;(defn first-day-of-next-month "" ([]) ([t]))
;(defn first-day-of-next-year "" ([]) ([t]))
;(defn first-day-of-year "" ([]) ([t]))
;(defn first-in-month "" ([day-of-week]) ([t day-of-week]))
;(defn last-day-of-month "" ([]) ([t]))
;(defn last-day-of-year "" ([]) ([t]))
;(defn last-in-month "" ([day-of-week]) ([t day-of-week]))

; weekdays - could be made to work probably
(defn next ""
  ([day-of-week]
   (get t/weekday-number->weekday
     (-> (t/weekday-number day-of-week)
         (inc)
         (mod 7))))
  ;([t day-of-week]
  ; ; todo
  ; )
  )
;(defn next-or-same "" ([day-of-week]) ([t day-of-week]))
#_(defn previous "" ([day-of-week]
                   (get t/weekday-number->weekday
                     (-> (t/weekday-number day-of-week)
                         (dec)) 7))
  ;([t day-of-week])
  )
;(defn previous-or-same "" ([day-of-week]) ([t day-of-week]))

(defn truncate "Returns a copy of x truncated to the specified unit." ([x u]
                                                                       (t/truncate x (get unit-map u))))

(defn current-clock "" ([] *clock*))

; not porting for now
;(defn tick-resolution "Obtains a clock that returns instants from the specified clock truncated to the nearest occurrence of the specified duration." ([clk]) ([clk dur]))
;(defn atom "construct atomic clock" ([clk]) ([]))
;(defn swap! "swap! on atomic clock 'at' " ([at f & args]))
;(defn swap-vals! "swap-vals! on atomic clock 'at' " ([at f & args]))
;(defn compare-and-set! "cas on atomic clock 'at' " ([at oldval newval]))
;(defn reset! "reset! on atomic clock 'at' " ([at newval]))
;(defn reset-vals! "reset-vals! on atomic clock 'at' " ([at newval]))


(defn >> "shift Temporal forward by n units"
  ;([t amount] (t/>> t amount))
  ([t n unit]
                                   (t/>> t n (get unit-map unit))))
(defn << "shift Temporal backward by n units"
  ;  ([t amount] (t/<< t amount))
  ([t n unit]
                                    (t/<< t n (get unit-map unit))))

; not porting. IMO there is no single logical quanity to inc/dec 
;(defn inc "" ([t]))
;(defn dec "" ([t]))

; superfluous
;(defn tomorrow "" ([]))
;(defn yesterday "" ([]))

(defn between "the amount of unit between v1 and v2" 
  #_([v1 v2] ; would return a temporal-amount. trying to avoid those
   (t/unt)) 
  ([v1 v2 unit]
   (t/until v1 v2 (get unit-map unit))))

;todo - intervals?
;(defn beginning "the beginning of the range of ITimeSpan v or v" ([v]))
;(defn end "the end of the range of ITimeSpan v or v" ([v]))
;(defn duration "return Duration or Period (whichever appropriate based on type) contained within the range of ITimeSpan x" ([x]))
;(defn backward-compatible-time-span-extensions "pre v0.7, ITimeSpan was extended as per this body. run this function to create those extensions.\n  \n  ITimeSpan is implemented by default on types with a natural beginning and end" ([]))
;(defn interval? "true if v is a interval?" ([v]))

;dont port
;(defn ago "current instant shifted back by duration 'dur'" ([dur]))
;(defn hence "current instant shifted forward by duration 'dur'" ([dur]))
;(defn midnight? "" ([t]))

;(defn clock? "true if v is a clock?" ([v] (t/clock? v)))
;(defn day-of-week? "true if v is a day-of-week?" ([v]))
(defn duration? "true if v is a duration?" ([v] (t/duration? v)))
(defn instant? "true if v is a instant?" ([v] (t/instant? v)))
(defn date? "true if v is a date?" ([v] (t/date? v)))
(defn date-time? "true if v is a date-time?" ([v] (t/datetime? v)))
(defn time? "true if v is a time?" ([v] (t/time? v)))
;(defn month? "true if v is a month?" ([v] ))
;(defn offset-date-time? "true if v is a offset-date-time?" ([v]))
(defn period? "true if v is a period?" ([v] (t/period? v)))
;(defn year? "true if v is a year?" ([v]))
(defn year-month? "true if v is a year-month?" ([v] (t/yearmonth? v)))
(defn zone? "true if v is a zone?" ([v] (t/timezone? v)))
;(defn zone-offset? "true if v is a zone-offset?" ([v] (t/zdt? v)))
(defn zoned-date-time? "true if v is a zoned-date-time?" ([v] (t/zdt? v)))


; no single logical int/long value for temporal entities - not implementing
;(defn int "" ([arg]))
;(defn long "" ([arg]))

(defn on "Set time be ON a date" ([t d]
                                  (t/date-from {:date d :time t})))

(defn at "Set date to be AT a time" ([d t]
                                     (on t d)))

(defn in "Set a date-time to be in a time-zone" ([ldt z]
                                                 (t/zdt-from {:datetime ldt :timezone_id z})))

(defn offset-by "Set a date-time to be offset by an amount" ([ldt offset]
                                                             (in ldt offset)))

(defn zone "" ([]
               (current-zone))
  ([z]
   ;todo - what type is z, string, zone, or zdt
   ))
(defn date "" ([]) ([v]))
(defn inst "" ([]) ([v]))
(defn instant "" ([]) ([v]))
(defn date-time "" ([]) ([v]))
(defn offset-date-time "" ([]) ([v]))
(defn zoned-date-time "" ([]) ([v]))
(defn nanosecond "extract nanosecond from t" ([t]))
(defn microsecond "extract microsecond from t" ([t]))
(defn millisecond "extract millisecond from t" ([t]))
(defn second "extract second from t" ([t]))
(defn minute "extract minute from t" ([t]))
(defn hour "extract hour from t" ([t]))
(defn time "extract time from v" ([]) ([v]))
(defn day-of-week "extract day-of-week from v" ([]) ([v]))
(defn day-of-month "extract day-of-month from v" ([]) ([v]))
(defn month "extract month from v" ([]) ([v]))
(defn year "extract year from v" ([]) ([v]))
(defn year-month "extract year-month from v" ([]) ([v]))
(defn clock "return i as a clock" ([]
                                   (t/clock-system-default-zone))
  ;([i] )
  )


#_(defn = "Same as clojure.core/=, but works on dates, rather than numbers.\n  can compare different types, e.g. Instant vs ZonedDateTime\n  " 
  ([_x]) ([x y]) ([x y & more]))
(def < ^{:doc "Same as clojure.core/<, but works on dates, rather than numbers"} t/<)
(def <= ^{:doc "Same as clojure.core/<=, but works on dates, rather than numbers"} t/<=)
(def > ^{:doc "Same as clojure.core/>, but works on dates, rather than numbers"} t/>)
(def ^{:doc "Same as clojure.core/>=, but works on dates, rather than numbers"} >= t/>=)
(defn greater "the greater of x and y" ([x y]
                                        (t/greater x y)))
(defn coincident? "for the 2-arity ver, Does containing-interval wholly contain the given contained-interval?\n  \n  for the 3-arity, does the event lie within the span of time described by start and end" 
  ;([containing-interval contained-interval]) 
  ([start end event]
   (and
     (<= start event)
     (>= end event))))
(defn max "Find the latest of the given arguments. Callers should ensure that no\n  argument is nil." 
  ([arg & args] (apply t/max arg args)))
(defn lesser "the lesser of x and y" ([x y]
                                      (t/lesser x y)))
(defn min "Find the earliest of the given arguments. Callers should ensure that no\n  argument is nil." 
  ([arg & args] (apply t/min arg args)))
(defn max-key "Same as clojure.core/max-key, but works on dates, rather than numbers" ([_k x]) ([k x y]) ([k x y & more]))
(defn min-key "Same as clojure.core/min-key, but works on dates, rather than numbers" ([_k x]) ([k x y]) ([k x y & more]))

;todo - tick alpha ns?
;temporal-amount stuff
;(defn divide "divide TemporalAmount t by divisor, which is a unit e.g. :hours or a TemporalAmount" ([t divisor]))
;(defn nanos "extract nanos from 'v'" ([v]))
;(defn micros "extract micros from 'v'" ([v]))
;(defn millis "extract millis from 'v'" ([v]))
;(defn hours "extract hours from 'v'" ([v]))
;(defn days "extract days from 'v'" ([v]))
;(defn months "extract months from 'v'" ([v]))
;(defn years "extract years from 'v'" ([v]))
;(defn negated "Return the duration as a negative duration" ([d]))
;(defn + "Sum amounts of time" ([]) ([arg]) ([arg & args]))
;(defn - "Subtract amounts of time." ([]) ([arg]) ([arg & args]))
;(defn new-duration "" ([n u]))
;(defn new-period "" ([n u]))
;(defn of-nanos "Takes a java.lang.Long n and returns a duration of n nanoseconds." ([n]))
;(defn of-micros "Takes a java.lang.Long n and returns a duration of n micros." ([n]))
;(defn of-millis "Takes a java.lang.Long n and returns a duration of n micros." ([n]))
;(defn of-seconds "Takes a java.lang.Long n and returns a duration of n seconds." ([n]))
;(defn of-minutes "Takes a java.lang.Long n and returns a duration of n minutes." ([n]))
;(defn of-hours "Takes a java.lang.Long n and returns a duration of n hours." ([n]))
;(defn of-days "Takes a java.lang.Long n and returns a period of n days." ([n]))
;(defn of-months "Takes a java.lang.Long n and returns a period of n months." ([n]))
;(defn of-years "Takes a java.lang.Long n and returns a period of n years." ([n]))
;(defn units "the units contained within TemporalAmount x.\n  \n  Seconds and nanos for Duration.\n  Years, months, days for Period\n  " ([x]))

; parsing/printing custom formats not available in Temporal yet
;(defn format "Formats the given time entity as a string.\n  Accepts something that can be converted to a `DateTimeFormatter` as a first\n  argument. Given one argument uses the default format." ([o]) ([fmt o]))
;(defn parse-day "en locale specific and borderline deprecated.\n  consider writing your own regex or use a formatter. For example:\n\n  (-> (t/formatter \"EEE\")\n      (cljc.java-time.format.date-time-formatter/parse \"Tue\")\n      (cljc.java-time.day-of-week/from))\n  " ([input]))
;(defn parse-month "en locale specific and borderline deprecated. Consider writing your\n   own regex or use a formatter. For example:\n\n   (-> (t/formatter \"MMM\")\n       (cljc.java-time.format.date-time-formatter/parse \"Jan\")\n       (cljc.java-time.month/from))\n   " ([input]))
;(defn parse-date "to parse an iso-formatted date, use (t/date \"2020..\") instead" ([date-str formatter]))
;(defn parse-date-time "to parse an iso-formatted date-time, use (t/date-time \"2020..\") instead" ([date-str formatter]))
;(defn parse-time "to parse an iso-formatted time, use (t/time \"20:20..\") instead" ([date-str formatter]))
;(defn parse-offset-date-time "to parse an iso-formatted offset-date-time, use (t/offset-date-time \"2020..\") instead" ([date-str formatter]))
;(defn parse-year "to parse an iso-formatted year, use (t/year \"2020\") instead" ([date-str formatter]))
;(defn parse-year-month "to parse an iso-formatted year-month, use (t/year-month \"2020..\") instead" ([date-str formatter]))
;(defn parse-zoned-date-time "to parse an iso-formatted zoned-date-time, use (t/zoned-date-time \"2020..\") instead" ([date-str formatter]))



#_(doseq [l (->> (string/split-lines (slurp "../tick/src/tick/core.cljc"))
                 (keep (fn [l] (when (string/starts-with? l "(defn ") l))))]
    (let [nstart (-> l (subs 6 (count l)))
          space (string/index-of nstart " ")
          f-name (symbol (if-not (nil? space)
                           (subs nstart 0 space)
                           nstart))
          {:keys [arglists doc name]} (some-> (ns-publics 'tick.core) (get f-name) meta)]
      (when arglists
        (pr
          `(~(symbol 'defn) ~name ~(or doc "") ~@(map list arglists)))
        (println ""))
      ))