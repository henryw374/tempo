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
    (-compare [x y] (let [d (.-sign ^js/Temporal.Duration (.until ^js x y))]
                      ;todo - is this right?
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

(defn extend-all []
  (plain-date)
  (duration))