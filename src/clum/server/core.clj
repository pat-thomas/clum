(ns clum.server.core
  (:require [org.httpkit.server :as http]
            [cheshire.core      :as json]
            [taoensso.timbre    :as log])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(defonce channels (atom #{}))
(defonce connected-clients (atom #{}))

(defn connect!
  [channel]
  (log/info "channel open")
  (swap! channels conj channel))

(defn disconnect!
  [{:strs [origin] :as req} channel status]
  (log/infof "channel closed: %s" status)
  (swap! connected-clients #(remove #{origin} %))
  (swap! channels #(remove #{channel} %)))

(defn notify-clients
  [msg]
  (let [serialized (json/generate-string msg)]
    (doseq [ch @channels]
      (http/send! ch serialized false))))

(defn play-animation
  [_]
  (loop [tick 0]
    (when (< tick 8)
      (log/infof "tick... %s" tick)
      (notify-clients {:tick tick})
      (Thread/sleep 500)
      (recur (inc tick)))))

(defn button-clicked
  [payload]
  (notify-clients {:highlighted (:data payload)}))

(def action-dispatch
  {"play-animation" #'play-animation
   "button-clicked" #'button-clicked})

(defn respond-msg
  [msg]
  (let [{:keys [action] :as parsed} (json/parse-string msg true)]
    (when-let [handler-fn (get action-dispatch action)]
      (handler-fn parsed))))

(defn running?
  [origin]
  (not (nil? (get @connected-clients origin))))

(defn handle-socket-request
  [{:keys [headers] :as req}]
  (let [{:strs [origin]} headers]
    (log/infof "handle-socket-request: %s" req)
    (http/with-channel req channel
      (connect! channel)
      (swap! channels conj channel)
      (if (running? origin)
        (log/infof "Client already connected, not spinning up new thread: %s" origin)
        (do
          (swap! connected-clients conj origin)
          (log/infof "Client NOT connected, spinning up new thread: %s" origin)
          (Thread.
           (loop [tick    0
                  measure 0]
             (log/infof "tick => %s, measure %s" tick measure)
             (notify-clients {:tick    tick
                              :measure measure})
             (Thread/sleep 200)
             (let [new-tick    (if (>= tick 7)
                                 0
                                 (inc tick))
                   new-measure (cond (and (>= tick 7)
                                          (>= measure 3))

                                     0

                                     (>= tick 7)
                                     (inc measure)

                                     :else
                                     measure)]
               (recur new-tick new-measure))))))
      (http/on-close channel #(partial disconnect! req channel))
      (http/on-receive channel (fn [msg]
                                 (respond-msg msg))))))

(defn app-fn
  [{:keys [uri] :as req}]
  (condp = uri
    "/socket"
    (handle-socket-request req)

    ;; else
    {:status  404}))

(def app
  #'app-fn)

(defn run-app
  []
  (http/run-server #'app {:port 8080}))

(comment
  (run-app)
  )
