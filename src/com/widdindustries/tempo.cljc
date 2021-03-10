(ns com.widdindustries.tempo
  #?(:clj
     (:import
       [java.util Date]
       [java.time Clock ZoneId ZoneOffset Instant Duration Period DayOfWeek Month ZonedDateTime LocalTime LocalDateTime LocalDate Year YearMonth ZoneId OffsetDateTime OffsetTime]
       [java.time.temporal Temporal TemporalAmount])))

(defn now-date
  ([] #?(:clj (LocalDate/now)
         :cljs (js/Temporal.now.plainDateISO)))
  ([clock]
   #?(:clj (LocalDate/now ^Clock clock)
      :cljs (.plainDateISO clock))))

(defn parse-date [s]
  #?(:clj (LocalDate/parse s)
     :cljs (js/Temporal.PlainDate.from s)))

(defn >> [temporal temporal-amount]
  #?(:clj (.plus  ^Temporal temporal ^TemporalAmount temporal-amount)
     :cljs (.add ^js temporal temporal-amount)))

(defn << [temporal temporal-amount]
  #?(:clj (.minus  ^Temporal temporal ^TemporalAmount temporal-amount)
     :cljs (.subtract ^js temporal temporal-amount)))

(defn parse-duration [s]
  #?(:clj (Duration/parse s)
     :cljs (js/Temporal.Duration.from s)))

(defn parse-period [s]
  #?(:clj (Period/parse s)
     :cljs (parse-duration s)))