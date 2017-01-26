(ns clum.server.core
  (:require [clum.server.midi-interface :as midi]
            [ring.middleware.transit :as transit-middleware]
            ;;[cognitect.transit :as transit]
            [org.httpkit.server :as http]
            [taoensso.timbre    :as log]))

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
  (def msg msg)
  (let [parsed-msg ()])
  (doseq [channel @channels]
    (midi/send-midi! msg)
    (http/send! channel msg)))

(defn handle-socket-request
  [req]
  (log/infof "handle-socket-request: %s" req)
  (http/with-channel req channel
    (connect! channel)
    (http/on-close channel #(partial disconnect! channel))
    (http/on-receive channel #(notify-clients %))))

(defn app-fn
  [{:keys [uri] :as req}]
  (condp = uri
    "/socket"
    (handle-socket-request req)

    ;; else
    {:status  404}))

(def app
  (-> #'app-fn
      transit-middleware/wrap-transit-body))

(comment
  (http/run-server #'app {:port 8080})
  )
