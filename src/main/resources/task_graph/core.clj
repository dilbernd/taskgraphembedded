(ns task-graph.core
  (:require [clojure.set :as sets]))

(defn acc-set "Adds another-val to set for key in map acc." [acc key another-val]
  (update acc key #(conj (or % #{}) another-val)))

(defn process-entry "Adds a dependency graph node's information to map acc." [acc [key val]]
  (if (empty? val) (acc-set acc :singles key)
                   (-> acc (acc-set :dependers key) (update :dependencies #(sets/union % val)))))

(defn node-by-types "Summarizes a dependency graph into sets by node type." [inputs] (reduce process-entry {} inputs))

(defn -execution-list "Main task list calculation function." [tasklist processed unprocessed dependers-map]
  (if (not (empty? unprocessed))
    (let [processable     (filter #(empty? (sets/difference (get dependers-map %) processed)) unprocessed)
          new-processed   (sets/union processed processable)
          new-unprocessed (sets/difference unprocessed processable)
          tlist           (into tasklist processable)]
      (recur tlist new-processed new-unprocessed dependers-map))
    tasklist))

(defn execution-list "Summarize graph; find start nodes; calculate executable task list itself." [dependers-map]
  (let [{singles :singles dependers :dependers dependencies :dependencies} (node-by-types dependers-map)
        starts (sets/union singles (sets/difference dependencies dependers))]
    (-execution-list (into [] starts) starts dependers dependers-map)))

(defn run-with-test-resource [] (-> (clojure.java.io/resource "test-data.edn") slurp read-string execution-list))
