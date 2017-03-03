(defproject clum "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-figwheel  "0.5.9"]
            [lein-cljsbuild "1.1.5"]
            [lein-auto      "0.1.3"]]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/clum/app"]
                        :figwheel     {:on-jsload "clum.app.core/on-js-reload"
                                       :open-urls ["http://localhost:3449/index.html"]}
                        :compiler     {:main                 clum.app.core
                                       :asset-path           "js/compiled/out"
                                       :output-to            "resources/public/js/compiled/clum.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :source-map-timestamp true
                                       :pretty-print         true
                                       :preloads             [devtools.preload]}}
                       {:id           "min"
                        :source-paths ["src/clum/app"]
                        :compiler     {:output-to     "resources/public/js/compiled/clum.js"
                                       :main          clum.app.core
                                       :optimizations :advanced
                                       :pretty-print  false}}]}
  :figwheel {:css-dirs ["resources/public/css"]}
  :auto {:default {:file-pattern #"\.(clj)$"}}
  
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.0"]
                                  [figwheel-sidecar "0.5.9"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src/clum/server" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}
  :dependencies [ ;; server-side
                 [org.clojure/clojure       "1.8.0"]
                 [http-kit                  "2.2.0"]
                 [com.taoensso/timbre       "4.8.0"]
                 [overtone                  "0.10.1"]
                 [cheshire                  "5.7.0"]

                 ;; client-side
                 [org.clojure/clojurescript  "1.9.494"]
                 [reagent                    "0.6.0"]
                 [cljs-bach                  "0.2.0"]
                 [com.cognitect/transit-cljs "0.8.239"]])
