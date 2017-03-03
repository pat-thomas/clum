(ns clum.app.state
  (:require [reagent.core :as r]))

(defonce app-state
  (let [initial-state (-> (reduce (fn [acc [x y]]
                                  (assoc acc [x y] {:highlighted? false}))
                                {}
                                (for [x (range 8)
                                      y (range 8)]
                                  [x y]))
                          (merge {:higlighted-set #{}
                                  :connected?     false}))]
    (r/atom initial-state)))

