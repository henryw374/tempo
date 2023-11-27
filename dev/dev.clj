(ns dev
  (:require [clojure.java.io :as io]
            [com.widdindustries.tiado-cljs2 :as util]
            [kaocha.repl :as kaocha]
            [clojure.tools.namespace.repl :as refresh]
            [com.widdindustries.gen.gen.tempo :as gen]))

(defn refresh []
  (refresh/refresh-all))

(defn x []
  (kaocha/run 'com.widdindustries.tempo-test))

(defn run-clj-tests [_]
  (refresh/refresh-all :after 'dev/x))

(comment 
  (refresh/clear)
  (gen/generate-all nil)
  (run-clj-tests nil)
  )

(defn browser-test-build [compile-mode opts]
  (.mkdirs (io/file "web-target" "public" "browser-test"))
  (spit "web-target/public/browser-test/index.html"
    "<!DOCTYPE html>
    <html><head>
    <title>kaocha.cljs2.shadow-runner</title>
    <meta charset=\"utf-8\">
    </head>
    <body>
   
    <script src=\"/temporal.js\"></script>
    
    <script src=\"/browser-test/js/test.js\">
    </script>
    <script>kaocha.cljs2.shadow_runner.init();</script></body></html>"
    )
  ((get util/compile-fns compile-mode)
   (util/browser-test-config) opts)
  (println "for tests, open " util/test-url))


(defn test-watch []
  (browser-test-build :watch {}))

(comment
  
  ; start up live-compilation of tests
  (test-watch)
  ; run cljs tests, having opened browser at test page (see print output of above "for tests, open...")
  (util/run-tests)
  ; start a cljs repl session in the test build. :cljs/quit to exit
  (util/repl :browser-test-build)
  
  ; show what npm libs needed by cljs libs
  (util/show-npm-deps)
  
  ; do the release build
  (app-release)

  (util/build-report (app-config) "build-report.html")

  ; you can stop/start etc as required
  (util/stop-server)
  (util/clean-build)
  )