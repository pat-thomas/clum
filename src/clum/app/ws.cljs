(ns clum.app.ws
  (:require [cognitect.transit :as t]))

(defonce ws-chan (atom nil))
(def json-reader (t/reader :json))

(defn receive-transit-msg!
  [update-fn]
  (fn [msg]
    (update-fn (->> msg .-data (t/read json-reader)))))

(defn ->json
  [thing]
  (.stringify js/JSON (clj->js thing)))

(defn update-app-state-from-socket!
  [state {:strs [message tick highlighted] :as data}]
  (when-not (nil? tick)
    (swap! state update-in [:tick] (fn [_]
                                         tick)))
  (when-not (nil? message)
    (swap! state update-in [:activity-log] (fn [activity-log]
                                                 (if (>= (count activity-log) 10)
                                                   (conj (drop-last activity-log) message)
                                                   (conj activity-log             message)))))

  (when-not (nil? highlighted)
    (let [{:strs [x y]}  highlighted
          coordinate-map {:x x
                          :y y}]
      (let [new-app-state (-> state
                              deref
                              (update-in [x y :highlighted] not)
                              (update-in [:highlighted-set] conj coordinate-map))]
        (reset! state new-app-state)))))

(defn send-transit-msg!
  [msg]
  (if @ws-chan
    (let [serialized-msg (->json msg)]
      (println serialized-msg)
      (.send @ws-chan serialized-msg))
    (throw (js/Error. "Websocket is not available!"))))

(defn make-websocket!
  [url receive-handler]
  (println "attempting to connect to websocket")
  (if-let [chan (js/WebSocket. url)]
    (do
      (set! (.-onmessage chan) (receive-transit-msg! receive-handler))
      (reset! ws-chan chan)
      (println "Websocket connection established with: " url))
    (throw (js/Error. "Websocket connection failed!"))))
