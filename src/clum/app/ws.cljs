(ns clum.app.ws
  (:require [clum.app.audio    :as audio]
            [cognitect.transit :as t]))

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
  (let [old-app-state @state
        new-app-state old-app-state
        new-app-state (if-not (nil? tick)
                        (update-in new-app-state [:tick] (fn [_]
                                                           tick))
                        new-app-state)
        new-app-state (if-not (nil? message)
                        (update-in new-app-state [:activity-log] (fn [activity-log]
                                                                   (if (>= (count activity-log) 10)
                                                                     (conj (drop-last activity-log) message)
                                                                     (conj activity-log             message))))
                        new-app-state)
        new-app-state (if-not (nil? highlighted)
                        (let [{:strs [x y]}  highlighted
                              coordinate-map {:x x
                                              :y y}]
                          (-> new-app-state
                              (update-in [x y :highlighted?] not)
                              (update-in [:highlighted-set] conj coordinate-map)))
                        new-app-state)]
    (audio/run-actions-for-tick tick new-app-state)
    (reset! state new-app-state)))

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
