(defproject clum "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "1.1.5"]]
  :cljsbuild {:builds [{:source-paths ["src/clum/app"]
                        :compiler     {:output-to "public/js/app.js"
                                       :optimizations :whitespace
                                       :pretty-print  true}}]}
  :dependencies [;; server-side
                 [org.clojure/clojure "1.8.0"]
                 [http-kit            "2.2.0"]

                 ;; client-side
                 [reagent                    "0.6.0"]
                 [com.cognitect/transit-cljs "0.8.239"]])
