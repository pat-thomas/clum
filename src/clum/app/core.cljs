(ns clum.app.core
  (:require [clum.app.components :as components]
            [clum.app.state      :as state]
            [clum.app.ws         :as ws]
            [clum.app.audio      :as audio]
            [reagent.core        :as r]))

(enable-console-print!)

(defn render-app
  [state]
  (r/render [#(components/main-component state)]
            (.getElementById js/document "app")))

(defn main
  [state]
  (if-not (:connected? @state)
    (do
      (println "not connected to websocket, connecting...")
      (ws/make-websocket! "ws://localhost:8080/socket" (partial ws/update-app-state-from-socket! state))
      (swap! state assoc :connected? true))
    (println "already connected to websocket, not reconnecting"))
  (render-app state))

(defn on-js-reload
  []
  (println "reloaded..."))

(main state/app-state)
