(ns com.widdindustries.gen.gen-in.constructors
  #?(:cljay (:import (java.time LocalDateTime ZonedDateTime))))

(defn datetime-from [thing])

(defn zdt-from [thing]
  (let [ldt (or (get thing :datetime )
              (datetime-from thing))
        zone (get thing :timezone)]
    #?(:cljay (ZonedDateTime/of ^LocalDateTime ldt ^ZoneId zone)
       :cljs (.toZonedDateTime ^js ldt zone)
       :cljc (cljc.java-time.zoned-date-time/of ^LocalDateTime ldt ^ZoneId zone))))

