(ns clum.app.components
  (:require [clum.app.ws    :as ws]
            [clum.app.audio :as audio]
            [reagent.core   :as r]))

(defn button
  [x y state]
  (let [component-state             (r/atom {:sound-playing? false})
        {:keys [highlighted? tick]} @state
        btn-class                   (cond highlighted?
                                          :div.app-button-highlighted

                                          (= tick y)
                                          :div.app-button-on

                                          :else
                                          :div.app-button)]
    [btn-class {:key      (str "button." x "." y)
                :on-click (fn [evt]
                            ;;(println "doing the thing with the stuff...")
                            ;;(audio/play-sine x y)
                            ;;(swap! component-state update-in [:sound-playing?] not)
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
  (let [{:keys [tick activity-log] :as state-value} @state]
    [:div#container
     [:div#buttons
      (doall
       (for [x (range 8)]
         [:div.button-row {:key x}
          (doall
           (for [y (range 8)]
             (button x y state)))]))]
     [anim-button]
     [#(app-debugger state)]
     [:div#activity-log
      (map (fn [{:keys [message]}]
             [:p message])
           activity-log)]]))
