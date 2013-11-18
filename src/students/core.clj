(ns students.core
  (:require [datomic.api :as d]))

(def uri "datomic:mem://student-test")

(d/create-database uri)

(def conn (d/connect uri))

;; parse schema edn file
(def schema-tx (read-string (slurp "resources/datomic/students-schema-simple.edn")))

;; submit schema transaction
@(d/transact conn schema-tx)

(let [josephine-id (d/tempid :db.part/user)
      marcel-id (d/tempid :db.part/user)
      test-id (d/tempid :db.part/user)]
  (d/transact conn
              [
               ;; add a couple of students
               {:db/id josephine-id
                :student/name "Josephine Müller"}
               {:db/id marcel-id
                :student/name "Marcel Meier"}

               ;; there was a test on Nov 15th
               {:db/id test-id
                :test/date (read-string "#inst \"2013-10-11\"")
                :test/weight 1.0
                :test/max-points 100}

               ;; Josephine did ok
               {:db/id (d/tempid :db.part/user)
                :score/points 95
                :score/test test-id
                :student/_scores josephine-id}

               ;; Marcel needs to study some more
               {:db/id (d/tempid :db.part/user)
                :score/points 40
                :score/test test-id
                :student/_scores marcel-id}]))


;; get all students
#_(d/q '[:find ?e
         :where
         [?e :student/name]] (d/db conn))
#_(d/q '[:find ?name
         :where
         [_ :student/name ?name]] (d/db conn))

;; get all tests
#_(d/q '[:find ?e
         :where
         [?e :test/date]] (d/db conn))
#_(d/q '[:find ?date ?weight ?points
         :where
         [?e :test/date ?date]
         [?e :test/weight ?weight]
         [?e :test/max-points ?points]] (d/db conn))

;; get all scores for a student
#_(d/q '[:find ?name ?score :where
       [?e :student/name ?name]
       [?e :student/scores ?score]
       ] (d/db conn))

;; get the max points that any student did in a particular test
#_(d/q '[:find ?t (max ?p) :where
       [?t :test/date]
       [?s :score/test ?t]
       [?s :score/points ?p]
       ] (d/db conn))

;; get the percentage for a student for a particular test
#_(d/q '[:find ?t (max ?p) :where
       [?t :test/date]
       [?s :score/test ?t]
       [?s :score/points ?p]
       ] (d/db conn))
