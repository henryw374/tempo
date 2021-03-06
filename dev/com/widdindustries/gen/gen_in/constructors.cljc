(ns com.widdindustries.gen.gen-in.constructors
  #?(:cljay (:import (java.time LocalDateTime ZonedDateTime))))

(defn time-from [thing]
  (let [^int hour   (get thing :hour 0)
        ^int minute (get thing :minute 0)
        ^int second (get thing :second 0)
        ^int nano   (get thing :nano 0)]
    #?(:cljay (LocalTime/of hour minute second nano)
       :cljs (js/Temporal.PlainTime. hour minute second 
               (-> (Math/floor (/ nano 1000)) (* 1000))
               (mod nano 1000))
       :cljc (cljc.java-time.local-time/of hour minute second nano))))

(defn date-from [thing]
  (let [year (or (-> (get thing :yearmonth)
                     yearmonth->year)
               (:year thing))
        month (or (-> (get thing :yearmonth)
                      yearmonth->month)
                (-> (get thing :monthday)
                    monthday->month)
                (:month thing))
        day (or (-> (get thing :monthday)
                    monthday->day-of-month)
              (get thing :day-of-month))]

    #?( :cljay (LocalDate/of ^int year ^int month ^int day)
      :cljs (js/Temporal.PlainDate. ^int year ^int month ^int day)
      :cljc (cljc.java-time.local-date/of ^int year ^int month ^int day))))

(defn datetime-from [thing]
  (let [date (or (get thing :date)
               (date-from thing))
        time (or (get thing :time)
               (time-from thing))]
    #?(:cljay (LocalDateTime/of ^LocalDate date ^LocalTime time)
       :cljs (.toPlainDateTime ^js date time)
       :cljc (cljc.java-time.local-date-time/of ^LocalDate date ^LocalTime time))))

(defn zdt-from [thing]
  (let [ldt (or (get thing :datetime )
              (datetime-from thing))
        zone (get thing :timezone)]
    #?(:cljay (ZonedDateTime/of ^LocalDateTime ldt ^ZoneId zone)
       :cljs (.toZonedDateTime ^js ldt zone)
       :cljc (cljc.java-time.zoned-date-time/of ^LocalDateTime ldt ^ZoneId zone))))

