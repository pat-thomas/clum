(ns clum.app.components
  (:require [clum.app.ws    :as ws]
            [clum.app.audio :as audio]
            [reagent.core   :as r]))

(defn button
  [x y state]
  (let [{:keys [tick] :as state-val} @state
        highlighted?                 (-> state-val
                                         (get-in [x y])
                                         :highlighted?
                                         true?)
        btn-class                    (cond highlighted?
                                           :div.app-button-highlighted

                                           (= tick y)
                                           :div.app-button-on

                                           :else
                                           :div.app-button)]
    [btn-class {:key      (str "button." x "." y)
                :on-click (fn [evt]
                            (ws/send-transit-msg! {:action "button-clicked"
                                                   :type   "broadcast"
                                                   :data   {:x x
                                                            :y y}}))}]))

(defn anim-button
  []
  [:input
   {:type     :button
    :value    "Play animation"
    :on-click #(ws/send-transit-msg! {:action "play-animation"})}])

(defn broadcast-button
  "Creates a button that will send a group of changes to the
   server to be broadcasted to all (or some) connected clients."
  []
  :implement-me)

(defn app-debugger
  [state]
  [:table
   [:thead>tr
    [:th "Tick"]]
   [:tbody
    [:tr
     [:td (:tick @state)]]]])

(defn button-row
  [state x]
  [:div.button-row {:key x}
   (doall
    (for [y (range 8)]
      (button x y state)))])

(defn main-component
  [state]
  (let [{:keys [tick activity-log] :as state-value} @state]
    [:div#container
     [:div#buttons
      (doall
       (for [x (range 8)]
         (button-row state x)))]
     ;;[anim-button]
     ;;[#(app-debugger state)]
     [:div#activity-log
      (map (fn [{:keys [message]}]
             [:p message])
           activity-log)]]))
