(ns clum.app.components
  (:require [clum.app.ws  :as ws]
            [reagent.core :as r]))

(defn button
  [state x y]
  (let [tick        (:tick @state)
        highlighted (:highlighted @state)
        btn-class   (cond (= highlighted
                             {:x x :y y})
                          :div.app-button-highlighted

                          (true? (-> state
                                     deref
                                     (get-in [x y])
                                     :highlighted))
                          :div.app-button-highlighted
                          
                          (or (= tick x)
                              (= tick y))
                          :div.app-button-on

                          :else
                          :div.app-button)]
    [btn-class {:key      (str "button." x "." y)
                :on-click (fn [evt]
                            (ws/send-transit-msg! {:action "button-clicked"
                                                   :type "broadcast"
                                                   :data   {:x x
                                                            :y y}}))}]))

(defn anim-button
  []
  [:input
   {:type     :button
    :value    "Play animation"
    :on-click #(ws/send-transit-msg! {:action "play-animation"})}])

(defn app-debugger
  [state]
  [:table
   [:thead>tr
    [:th "Tick"]]
   [:tbody
    [:tr
     [:td (:tick @state)]]]])

(defn main-component
  [state]
  [:div#container
   [:div#buttons
    (for [x (range 8)]
      [:div.button-row {:key x}
       (for [y (range 8)]
         (button state x y))])]
   [anim-button]
   [#(app-debugger state)]
   [:div#activity-log
    (map (fn [{:keys [message]}]
           [:p message])
         (:activity-log @state))]])
