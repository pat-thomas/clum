(ns clum.app.core
  (:require [clum.app.ws  :as ws]
            [reagent.core :as r]))

(enable-console-print!)

(def app-state (r/atom {}))

(defn button
  [x y]
  [:div.app-button {;;:data-x   x
                    ;;:data-y   y
                    :key      (str "button." x "." y)
                    :on-click (fn [evt]
                                (ws/send-transit-msg! {:action "button-clicked"
                                                       :data   {:x x
                                                                :y y}}))}])

(defn message-input
  []
  (let [value (r/atom nil)]
    (fn []
      [:input.form-control
       {:type        :text
        :placeholder "type in a message and press enter!"
        :value       @value
        :on-change   #(reset! value (-> % .-target .-value))
        :on-key-down #(when (= (.-keyCode %) 13)
                        (ws/send-transit-msg! {:message @value})
                        (reset! value nil))}])))

(defn activity-log-view
  []
  (mapv (fn [txt]
          [:p txt])
        (:activity-log @app-state)))

(defn main-component
  []
  [:div#container
   [:div#buttons
    (for [x (range 8)]
      [:div.button-row {:key x}
       (for [y (range 8)]
         (button x y))])]
   [message-input]
   [:div#activity-log
    (map (fn [{:keys [message]}]
           [:p message])
         (:activity-log @app-state))]])

(defn render-app
  []
  (r/render [main-component]
            (.getElementById js/document "app")))

(defn update-messages!
  [data]
  (swap! app-state update-in [:activity-log] (fn [activity-log]
                                               (if (>= (count activity-log) 10)
                                                 (conj (drop-last activity-log) data)
                                                 (conj activity-log             data))))
  (println (:activity-log @app-state)))

(defn main
  []
  (ws/make-websocket! "ws://localhost:8080/socket" update-messages!)
  (render-app))

(main)
