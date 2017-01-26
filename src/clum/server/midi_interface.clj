(ns clum.server.midi-interface
  (:require [taoensso.timbre :as log]))

(defn send-midi!
  [msg]
  (log/infof "IMPLEMENTME: send-midi! %s" msg))
