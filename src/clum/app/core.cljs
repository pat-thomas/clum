(ns clum.app.core
  (:require [clum.app.components :as components]
            [clum.app.state      :as state]
            [clum.app.ws         :as ws]
            [reagent.core        :as r]))

(enable-console-print!)

(defn render-app
  [state]
  (r/render [#(components/main-component state)]
            (.getElementById js/document "app")))

(defn main
  [state]
  (ws/make-websocket! "ws://localhost:8080/socket" (partial ws/update-app-state-from-socket! state))
  (render-app state))

(main state/app-state)
