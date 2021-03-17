(ns com.widdindustries.tempo.cljs-protocols)

(defn plain-date []
  (extend-protocol IEquiv
    js/Temporal.PlainDate
    (-equiv [o other] (.equals ^js o other)))

  (extend-protocol IHash
    js/Temporal.PlainDate
    (-hash [o] (hash (str o))))

  (extend-protocol IComparable
    js/Temporal.PlainDate
    (-compare [x y] (let [d (.-sign (.negated (.until ^js x y)))]
                      d))))

(defn duration []
  (extend-protocol IHash
    js/Temporal.Duration
    (-hash [o] (hash (str o))))
  
  (extend-protocol IEquiv
    js/Temporal.Duration
    (-equiv [o other] (zero? (compare o other))))

  (extend-protocol IComparable
    js/Temporal.Duration
    (-compare [x y] (js/Temporal.Duration.compare x y)))

  )

(defn timezone []
  (extend-protocol IEquiv
    js/Temporal.TimeZone
    (-equiv [o other] (= (.-id ^js/TemporalThing o) (.-id ^js/TemporalThing other))))

  (extend-protocol IHash
    js/Temporal.TimeZone
    (-hash [o] (hash (.-id ^js/TemporalThing o))))

  )

(defn extend-all []
  (plain-date)
  (duration)
  (timezone))