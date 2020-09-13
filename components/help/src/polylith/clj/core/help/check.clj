(ns polylith.clj.core.help.check
  (:require [polylith.clj.core.help.shared :as shared]
            [polylith.clj.core.util.interface.color :as color]))

(defn help-text [color-mode]
  (str "  Performs various checks against the workspace.\n"
       "  " (color/warning color-mode "Warnings") ", checks that:\n"
       "  - Functions and macros for an interface has a matching parameter list\n"
       "    (with the same name and order) in all implementing components.\n"
       "  " (color/error color-mode "Errors") ", checks that:\n"
       "  - Components are only accessed vid their interface namespace via the " (color/purple color-mode ":require") "\n"
       "    statement, e.g.:\n "
       "   - " (shared/interface-ns "interface" color-mode) " \n "
       "   - " (shared/interface-ns "interface.subns" color-mode) " \n "
       "   - " (shared/interface-ns "interface" color-mode) " \n "
       "   - " (shared/interface-ns "interface.my.subns" color-mode) " \n"
       "  - No circular dependencies exists.\n"
       "  - A definition (def/defn/defmacro) that is defined in one component's interface must\n"
       "    also be defined in all other components that implements that interface.\n"
       "  - Only one component per interface exists in each environment.\n"
       "  - All environments has the components they need, based on referred interfaces.\n"
       "  - No bases share the name of an interface or component.\n"
       "  - Functions and macros are only defined once in each interface.\n"))

(defn print-help [color-mode]
  (-> color-mode help-text println))