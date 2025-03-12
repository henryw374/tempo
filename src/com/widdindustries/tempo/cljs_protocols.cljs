(ns
 com.widdindustries.tempo.cljs-protocols
 ""
 (:refer-clojure :exclude [time])
 (:require
  [com.widdindustries.tempo.js-temporal-entities :as entities]))

(defn
 duration
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
 instant
 []
 (extend-protocol
  IEquiv
  entities/instant
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash entities/instant (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  entities/instant
  (-compare [x y] (.compare entities/instant ^js x y))))

(defn
 zdt
 []
 (extend-protocol
  IEquiv
  entities/zdt
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash entities/zdt (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  entities/zdt
  (-compare [x y] (.compare entities/zdt ^js x y))))

(defn
 date
 []
 (extend-protocol
  IEquiv
  entities/date
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash entities/date (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  entities/date
  (-compare [x y] (.compare entities/date ^js x y))))

(defn
 datetime
 []
 (extend-protocol
  IEquiv
  entities/datetime
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash entities/datetime (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  entities/datetime
  (-compare [x y] (.compare entities/datetime ^js x y))))

(defn
 time
 []
 (extend-protocol
  IEquiv
  entities/time
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash entities/time (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  entities/time
  (-compare [x y] (.compare entities/time ^js x y))))

(defn
 monthday
 []
 (extend-protocol
  IEquiv
  entities/monthday
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash entities/monthday (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  entities/monthday
  (-compare
   [^js x ^js y]
   (let
    [m (compare (.-monthCode x) (.-monthCode y))]
    (if (zero? m) (compare (.-day x) (.-day y)) m)))))

(defn
 yearmonth
 []
 (extend-protocol
  IEquiv
  entities/yearmonth
  (-equiv [o other] (.equals ^js o other)))
 (extend-protocol IHash entities/yearmonth (-hash [o] (hash (str o))))
 (extend-protocol
  IComparable
  entities/yearmonth
  (-compare [x y] (.compare entities/yearmonth ^js x y))))

(defn
 extend-all
 []
 (instant)
 (zdt)
 (date)
 (datetime)
 (time)
 (monthday)
 (yearmonth)
 (duration))

