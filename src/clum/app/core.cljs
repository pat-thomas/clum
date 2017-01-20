(ns clum.app.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(def app-state (r/atom {}))

(defn button
  []
  [:div.app-button {:on-click #(println "hello")}])

(defn main-component
  []
  [:div#container
   [:div.button-row
    (button)
    (button)]])

(defn render-app
  []
  (r/render [main-component]
            (.getElementById js/document "app")))

(render-app)
