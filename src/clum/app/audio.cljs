(ns clum.app.audio
  (:require [cljs-bach.synthesis :as syn]))

(defonce context (syn/audio-context))

(def frequency-dispatch
  {0 440
   1 220
   2 440
   3 220
   4 440 
   5 220
   6 440
   7 880})

(defn ->frequency
  [y x]
  (let [freq (get frequency-dispatch x)]
    freq))

(def volume-dispatch
  {0 0.5
   1 0.45
   2 0.4
   3 0.5
   4 0.4
   5 0.3
   6 0.5
   7 0.25})

(defn ->volume
  [y x]
  (let [volume (get volume-dispatch y)]
    (println "volume" volume)
    y))

(defn play-synth
  [synth x y]
  (let [frequency     (->frequency y x)
        volume        (->volume y x)
        created-synth (syn/connect->
                       (synth frequency)
                       (syn/adsr 0.3 0.2 0.01 0.005)
                       ;;(syn/low-pass 1000)
                       (syn/gain volume))]
    (-> created-synth
        (syn/connect-> syn/destination)
        (syn/run-with context (syn/current-time context) 1.0))))

(def synth-dispatch
  {0 syn/sine
   1 syn/triangle
   2 syn/square
   3 syn/sine
   4 syn/triangle
   5 syn/square
   6 syn/sawtooth
   7 syn/triangle})

(defn dispatch-action
  [i tick]
  (let [synth-fn (get synth-dispatch i)]
    #(play-synth synth-fn i tick)))

(defn run-actions-for-tick
  [tick state]
  (when tick
    (doseq [i (range 8)]
      (let [highlighted? (-> state
                             (get-in [i tick])
                             :highlighted?
                             true?)]
        (when-let [action-fn (and highlighted?
                                  (dispatch-action i tick))]
          (action-fn i tick))))))
