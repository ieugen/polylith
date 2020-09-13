(ns polylith.clj.core.workspace-clj.core
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.workspace-clj.profile :as profile]
            [polylith.clj.core.path-finder.interface :as path-finder]
            [polylith.clj.core.workspace-clj.bases-from-disk :as bases-from-disk]
            [polylith.clj.core.workspace-clj.environment-from-disk :as envs-from-disk]
            [polylith.clj.core.workspace-clj.components-from-disk :as components-from-disk]))

(defn stringify-key-value [[k v]]
  [(str k) (str v)])

(defn stringify [ns->lib]
  (into {} (mapv stringify-key-value ns->lib)))

(defn workspace-from-disk
  ([user-input]
   (let [ws-dir (common/workspace-dir user-input)
         config (read-string (slurp (str ws-dir "/deps.edn")))]
     (workspace-from-disk ws-dir config user-input)))
  ([ws-dir {:keys [polylith aliases]} user-input]
   (let [{:keys [vcs top-namespace interface-ns default-profile-name stable-since-tag-pattern env->alias ns->lib]} polylith
         top-src-dir (-> top-namespace common/suffix-ns-with-dot common/ns-to-path)
         color-mode (or (:color-mode user-input) (user-config/color-mode) color/none)
         empty-char (user-config/empty-character)
         thousand-sep (user-config/thousand-separator)
         home-dir (user-config/home-dir)
         component-names (file/directory-paths (str ws-dir "/components"))
         components (components-from-disk/read-components ws-dir top-src-dir component-names interface-ns)
         bases (bases-from-disk/read-bases ws-dir top-src-dir)
         environments (envs-from-disk/read-environments ws-dir)
         profile->settings (profile/profile->settings aliases)
         paths (path-finder/paths ws-dir environments profile->settings)
         settings (util/ordered-map :vcs (or vcs "git")
                                    :project-url "https://github.com/tengstrand/polylith"
                                    :file-extensions [".clj" "cljc"]
                                    :ws-contract-version "0.0"
                                    :color-mode color-mode
                                    :empty-char empty-char
                                    :thousand-sep thousand-sep
                                    :home-dir home-dir
                                    :top-namespace top-namespace
                                    :interface-ns (or interface-ns "interface")
                                    :default-profile-name (or default-profile-name "default")
                                    :stable-since-tag-pattern (or stable-since-tag-pattern "stable-*")
                                    :profile->settings profile->settings
                                    :env->alias env->alias
                                    :ns->lib (stringify ns->lib)
                                    :user-input user-input)]
     {:ws-dir ws-dir
      :settings settings
      :components components
      :bases bases
      :environments environments
      :paths paths})))