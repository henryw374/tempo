(ns noodle.client-app
  (:require
    [com.widdindustries.tempo :as t]
    )
  )

;(t/extend-all-cljs-protocols)
;(cljs-protocols/instant)
;(cljs-protocols/zdt)



(js/console.log " " (= (t/clock-system-default-zone)
                      [] (t/instant-from {} )
                      )
  )
