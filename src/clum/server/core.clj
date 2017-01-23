(ns clum.server.core
  (:require [org.httpkit.server :as http]))

(defonce channels (atom #{}))

(defn connect!
  [channel]
  (println "channel open")
  (swap! channels conj channel))

(defn disconnect!
  [channel status]
  (println (format "channel closed: %s" status))
  (swap! channels #(remove #{channel} %)))

(defn notify-clients
  [msg]
  (def msg msg)
  (doseq [channel @channels]
    (http/send! channel msg)))

(defn handle-socket-request
  [req]
  (def rr req)
  (http/with-channel req channel
    (connect! channel)
    (http/on-close channel #(partial disconnect! channel))
    (http/on-receive channel #(notify-clients %))))

(defn app
  [{:keys [uri] :as req}]
  (condp = uri
    "/socket"
    (handle-socket-request req)

    ;; else
    {:status  404}))


(comment
  (http/run-server #'app {:port 8080})
  )
