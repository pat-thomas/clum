(ns clum.app.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(def app-state (r/atom {}))

(defn main-component
  []
  [:div#container
   [:div.button-row
    [:div.app-button]]])

(defn render-app
  []
  (r/render [main-component]
            (.getElementById js/document "app")))

(render-app)
