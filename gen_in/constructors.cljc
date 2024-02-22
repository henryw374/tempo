(ns com.widdindustries.gen.gen-in.constructors
  #?(:cljay (:import (java.time LocalDateTime ZonedDateTime)))
  (:require [com.widdindustries.tempo :as t]
            [com.widdindustries.tempo :as t]))

(defn time-from [thing]
  (let [hour (get thing :hour 0)
        minute (get thing :minute 0)
        second (get thing :second 0)
        milli (get thing :millisecond 0)
        micro (get thing :microsecond 0)
        nano (get thing :nanosecond 0)]
    #?(:cljay (LocalTime/of ^int hour ^int minute ^int second ^int (+ (* milli 1000000)
                                                                     (* micro 1000)
                                                                     nano))
       :cljs (js/Temporal.PlainTime. hour minute second
               milli micro nano))))

(defn date-from [thing]
  (let [year (or (some-> (get thing :yearmonth)
                   yearmonth->year)
               (:year thing))
        month (or (some-> (get thing :yearmonth)
                    yearmonth->month)
                (some-> (get thing :monthday)
                  monthday->month)
                (:month thing))
        day (or (some-> (get thing :monthday)
                  monthday->day-of-month)
              (get thing :day-of-month))]

    #?(:cljay (LocalDate/of ^int year ^int month ^int day)
       :cljs (js/Temporal.PlainDate. ^int year ^int month ^int day))))

(defn datetime-from [thing]
  (let [date (or (get thing :date)
               (date-from thing))
        time (or (get thing :time)
               (time-from thing))]
    #?(:cljay (LocalDateTime/of ^LocalDate date ^LocalTime time)
       :cljs (.toPlainDateTime ^js date time))))

(defn zdt-from [thing]
  (let [ldt (or (get thing :datetime)
              (datetime-from thing))
        zone   (get thing :timezone_id)]
    #?(:cljay (ZonedDateTime/of ^LocalDateTime ldt ^ZoneId (timezone-parse zone))
       :cljs (.toZonedDateTime ^js ldt zone))))

(defn instant-from [thing]
  (or (some-> (get thing :epochmilli)
        #?(:cljay (Instant/ofEpochMilli)
           :cljs (js/Temporal.Instant.fromEpochMilliseconds)))
    (when-let [d (get thing :legacydate)]
      #?(:cljay (.toInstant ^Date d)
         :cljs (.toTemporalInstant ^js d)))
    (some-> (or (get thing :zdt) (zdt-from thing))
      (zdt->instant))))

