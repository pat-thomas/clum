(ns clum.server.core
  (:require [clum.server.midi-interface :as midi]
            [org.httpkit.server :as http]
            [cheshire.core     :as json]
            [taoensso.timbre    :as log])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(defonce channels (atom #{}))

(defn connect!
  [channel]
  (log/info "channel open")
  (swap! channels conj channel))

(defn disconnect!
  [channel status]
  (log/infof "channel closed: %s" status)
  (swap! channels #(remove #{channel} %)))

(defn notify-clients
  [msg]
  (let [serialized (json/generate-string msg)]
    (doseq [ch @channels]
      (http/send! ch serialized false))))

(defn handle-socket-request
  [req]
  (log/infof "handle-socket-request: %s" req)
  (def req req)
  (http/with-channel req channel
    (connect! channel)
    (swap! channels conj channel)
    (http/on-close channel #(partial disconnect! channel))

    (loop [tick 0]
      (when (< tick 8)
        (log/infof "tick... %s" tick)
        (notify-clients {:tick tick})
        (Thread/sleep 500)
        (recur (inc tick))))
    
    ;;(http/on-receive channel #(notify-clients %))
    (http/on-receive channel (fn [msg]
                               (def msg msg)))))

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
