(ns
 com.widdindustries.tempo.temporal-comparison
 "functions to make clojure's comparison functions work for Temporal entities"
 (:refer-clojure :exclude [time]))

(defn
 enable-duration
 []
 (extend-protocol
  IHash
  js/Temporal.Duration
  (-hash [o] (hash (str o))))
 (extend-protocol
  IEquiv
  js/Temporal.Duration
  (-equiv [o other] (zero? (compare o other))))
 (extend-protocol
  IComparable
  js/Temporal.Duration
  (-compare [x y] (js/Temporal.Duration.compare ^js x y))))

(defn
 enable-instant
 []
 (extend-protocol
  IEquiv
  js/Temporal.Instant
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash js/Temporal.Instant (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
   js/Temporal.Instant
  (-compare [x y] (.compare js/Temporal.Instant ^js x y))))

(defn
 enable-zdt
 []
 (extend-protocol
  IEquiv
  js/Temporal.ZonedDateTime
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash js/Temporal.ZonedDateTime (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  js/Temporal.ZonedDateTime
  (-compare [x y] (.compare js/Temporal.ZonedDateTime ^js x y))))

(defn
 enable-date
 []
 (extend-protocol
  IEquiv
  js/Temporal.PlainDate
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash js/Temporal.PlainDate (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  js/Temporal.PlainDate
  (-compare [x y] (.compare js/Temporal.PlainDate ^js x y))))

(defn
 enable-datetime
 []
 (extend-protocol
  IEquiv
  js/Temporal.PlainDateTime
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash js/Temporal.PlainDateTime (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  js/Temporal.PlainDateTime
  (-compare [x y] (.compare js/Temporal.PlainDateTime ^js x y))))

(defn
 enable-time
 []
 (extend-protocol
  IEquiv
  js/Temporal.PlainTime
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash js/Temporal.PlainTime (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  js/Temporal.PlainTime
  (-compare [x y] (.compare js/Temporal.PlainTime ^js x y))))

(defn
 enable-monthday
 []
 (extend-protocol
  IEquiv
  js/Temporal.PlainMonthDay
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash js/Temporal.PlainMonthDay (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  js/Temporal.PlainMonthDay
  (-compare
   [^js x ^js y]
   (let
    [m (compare (.-monthCode x) (.-monthCode y))]
    (if (zero? m) (compare (.-day x) (.-day y)) m)))))

(defn
 enable-yearmonth
 []
 (extend-protocol
  IEquiv
  js/Temporal.PlainYearMonth
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash js/Temporal.PlainYearMonth (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  js/Temporal.PlainYearMonth
  (-compare [x y] (.compare js/Temporal.PlainYearMonth ^js x y))))

(defn enable-for-all []
 (enable-instant)
 (enable-zdt)
 (enable-date)
 (enable-datetime)
 (enable-time)
 (enable-monthday)
 (enable-yearmonth)
 (enable-duration))

