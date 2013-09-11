(ns leiningen.new.skel
  (:use [leiningen.new.templates :only [renderer name-to-path year multi-segment
                                        project-name sanitize-ns ->files]])
  (:require [clojure.string :as string]
            [clojure.pprint :as pp]))

(defn group-name
  "Returns the group-name of the project"
  [s]
  (let [ssplit (string/split s #"/")]
    (if (> (count ssplit) 1)
      (first ssplit)
      nil)))

(defn skel
  "A custom template for my projects"
  [name & args]
  (let [raw-name name
        group (or (group-name raw-name) "co.grubb")
        name (project-name raw-name)
        project (string/join "/" [group name])
        msname (multi-segment name)
        fqname (string/join "/" [group msname])
        description (or (first args) "FIXME: Project description")
        version (or (second args) "0.1.0-SNAPSHOT")
        path (name-to-path msname)
        namespace (sanitize-ns msname)
        data {:raw-name raw-name
              :name name
              :group group
              :fqname fqname
              :project project
              :path (name-to-path msname)
              :namespace (sanitize-ns msname)
              :year (year)
              :version version
              :description description}
          rfn (renderer "skel")
        render #(vector %1 (rfn %2 data))]
    (->files data
             (render ".gitignore" "gitignore")
             (render "project.clj" "project.clj")
             (render "src/{{path}}.clj" "core.clj")
             (render "test/{{path}}_test.clj" "test.clj")
             (render "dev/user.clj" "user.clj")
             (render "README.md" "README.md")
             (render "LICENSE" "LICENSE"))))
