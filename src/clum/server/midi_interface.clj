(ns clum.server.midi-interface
  (:require [taoensso.timbre      :as log]
            [overtone.studio.midi :as midi]))

(comment
  (midi/midi-device-keys)
)

(defn send-midi!
  [msg]
  (log/infof "IMPLEMENTME: send-midi! %s" msg))
