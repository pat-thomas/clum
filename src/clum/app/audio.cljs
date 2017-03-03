(ns clum.app.audio
  (:require [cljs-bach.synthesis :as syn]))

(def context (syn/audio-context))

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
  {0 0.05
   1 0.1
   2 0.15
   3 0.2
   4 0.25
   5 0.3
   6 0.4
   7 0.5})

(defn ->volume
  [y x]
  (let [volume (get volume-dispatch y)]
    (println "volume:" volume)
    y))

(defn create-sine
  [x y]
  (let [frequency (->frequency y x)
        volume    (->volume y x)]
    (syn/connect->
     (syn/sine frequency)
     (syn/percussive 0.01 0.4)
     (syn/gain volume))))

(defn play-sine
  [x y]
  (-> (create-sine x y)
      (syn/connect-> syn/destination)
      (syn/run-with context (syn/current-time context) 1.0)))
