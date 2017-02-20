(ns clum.app.core
  (:require [clum.app.ws  :as ws]
            [reagent.core :as r]))

(enable-console-print!)

(def app-state
  (let [initial-state (reduce (fn [acc [x y]]
                                (assoc acc [x y] {:highlighted false}))
                              {}
                              (for [x (range 8)
                                    y (range 8)]
                                [x y]))]
    (r/atom initial-state)))

(defn button
  [x y]
  (let [tick        (:tick @app-state)
        highlighted (:highlighted @app-state)
        btn-class   (cond (= highlighted
                             {:x x :y y})
                          :div.app-button-highlighted

                          (true? (-> app-state
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

(defn anim-button
  []
  [:input
   {:type     :button
    :value    "Play animation"
    :on-click #(ws/send-transit-msg! {:action "play-animation"})}])

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
   [anim-button]
   [:div#activity-log
    (map (fn [{:keys [message]}]
           [:p message])
         (:activity-log @app-state))]])

(defn render-app
  []
  (r/render [main-component]
            (.getElementById js/document "app")))

(defn update-app-state-from-socket!
  [{:strs [message tick highlighted] :as data}]
  (when-not (nil? tick)
    (swap! app-state update-in [:tick] (fn [_]
                                         tick)))
  (when-not (nil? message)
    (swap! app-state update-in [:activity-log] (fn [activity-log]
                                                 (if (>= (count activity-log) 10)
                                                   (conj (drop-last activity-log) message)
                                                   (conj activity-log             message)))))

  (when-not (nil? highlighted)
    (let [{:strs [x y]} highlighted]
      (let [new-app-state (-> app-state
                              deref
                              (update-in [:highlighted] (fn [_]
                                                          {:x x :y y}))
                              (update-in [x y :highlighted] not))]
        (reset! app-state new-app-state)))))

(defn main
  []
  (ws/make-websocket! "ws://localhost:8080/socket" update-app-state-from-socket!)
  (render-app))

(main)
