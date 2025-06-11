(ns noodle.client-app
  (:require
    [com.widdindustries.tempo :as t]
    )
  )

;(t/enable-comparison-for-all-temporal-entities)
;(cljs-protocols/instant)
;(cljs-protocols/zdt)



(js/console.log " " (= (t/clock-system-default-zone)
                      [] (t/instant-from {} )
                      )
  )
