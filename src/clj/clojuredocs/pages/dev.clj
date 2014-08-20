(ns clojuredocs.pages.dev
  (:require [clojure.string :as str]
            [compojure.core :refer [defroutes GET]]
            [clojuredocs.pages.common :as common]
            [clojuredocs.pages.quickref :as quickref]
            [clojuredocs.schemas :as schemas]
            [schema.core :as s]
            [clojuredocs.util :as util]))

(defn section [title & body]
  [:secton
   [:h2 title]
   body])

(defn $section [{:keys [title nav-target content]}]
  [:section.styleguide-section {:id nav-target}
   [:h2 title]
   (vec (concat [:div.section-content] content))])

(defn $nav [title sections]
  [:div
   {:data-sticky-offset "20"}
   [:h5 title]
   [:ul
    [:li [:h6 [:a {:href "#"
                   :data-animate-scroll "true"} "Top"]]]
    (for [{:keys [nav-target title]} sections]
      [:li
       [:h6
        [:a {:href (str "#" nav-target)
             :data-animate-scroll "true"
             :data-animate-buffer "20"} title]]])]])

(defn $tpl [{:keys [body-class user page-uri nav content]}]
  (common/$main
    {:body-class "dev-page"
     :user user
     :page-uri page-uri
     :content
     [:div.row
      [:div.col-md-2
       [:div.sidenav
        [:section
         [:h5 "Dev"]
         [:ul
          [:li [:a {:href "/dev/api"} "API Docs"]]
          #_[:li [:a {:href "/dev/search-perf"} "Search Perf"]]
          #_[:li [:a {:href "/dev/canary"} "Canary Tests"]]]]
        [:section
         [:h5 "Styleguide"]
         [:ul
          [:li [:a {:href "/dev/styleguide"} "General"]]
          [:li [:a {:href "/dev/styleguide/search"} "Search"]]
          [:li [:a {:href "/dev/styleguide/examples"} "Examples"]]
          ]]
        nav]]
      [:div.col-md-10
       content]]}))

(defn example [{:keys [title hook caption]}]
  [:div.sg-example
   (when title
     [:h4 title])
   [:div {:class (str "checker-bg " (name hook))}]
   (when caption
     [:div.caption caption])])

(def examples-styleguide-sections
  [{:title "Rendering"
    :nav-target "rendering"
    :content
    [(->> [{:hook :sg-examples-null-state
            :caption "null state for clojure.core/map"}
           {:hook :sg-examples-single
            :caption "single example"}
           {:hook :sg-examples-lengths
            :caption "examples of varying length"}]
          (map example))]}
   {:title "Creating"
    :nav-target "creating"
    :content
    [(->> [{:hook :sg-add-example
            :caption "adding an example"}
           {:hook :sg-add-example-loading
            :caption "loading"}
           {:hook :sg-add-example-errors
            :caption "with general error"}]
          (map example))]}
   {:title "Editing"
    :nav-target "editing"
    :content
    [(->> [{:hook :sg-edit-example
            :caption "editing"}]
          (map example))]}
   {:title "Deleting"
    :nav-target "deleting"
    :content
    [[:p "Delete is available when you're the original author of an example"]
     (->> [{:hook :sg-delete-example
            :caption "initial view"}
           {:hook :sg-delete-example-confirm
            :caption "with confirmation"}
           {:hook :sg-delete-example-loading
            :caption "loading state"}
           {:hook :sg-delete-example-error
            :caption "with error"}]
          (map example))]}])

(def search-styleguide-sections
  [{:title "Search Bar"
    :nav-target "quick-search"
    :content
    [[:p "The main search widget on the home page. This should feel immediately accessible and get users where they want to go, fast."]
     [:div.example.checker-bg.sg-quick-lookup]
     [:p "When loading autocomplete"]
     [:div.example.checker-bg.sg-quick-lookup-loading]
     [:p "Null state"]
     [:div.example.checker-bg.sg-quick-lookup-null-state]]}
   {:title "Live Search"
    :nav-target "quick-search"
    :content
    [[:p "Landing search w/ autocomplete: heterogenous results linking to vars, namespaces, and concept pages."]
     [:div.example.checker-bg.sg-quick-lookup-autocomplete]]}])

(def styleguide-sections
  (concat
    [{:title "Bootstrap"
      :nav-target "bootstrap-overrides"
      :content
      [[:p
        "ClojureDocs is built on, among other things, the amazing "
        [:a {:href "https://getbootstrap.com"} "Bootstrap framework"]
        ". In this section, you'll find the ways we've overridden the Bootstrap defaults."]
       [:section.headers-ex
        [:h3 "Headers"]
        [:h1 "h1. Heading 1 " [:small "With small"]]
        [:h2 "h2. Heading 2 " [:small "With small"]]
        [:h3 "h3. Heading 3 " [:small "With small"]]
        [:h4 "h4. Heading 4 " [:small "With small"]]
        [:h5 "h5. Heading 5 " [:small "With small"]]
        [:h6 "h6. Heading 6 " [:small "With small"]]]
       [:section.buttons-ex
        [:h3 "Buttons"]
        [:button.btn.btn-default "Default"]
        [:button.btn.btn-primary "Primary"]
        [:button.btn.btn-success "Success"]
        [:button.btn.btn-info "Info"]
        [:button.btn.btn-warning "Warning"]
        [:button.btn.btn-danger "Danger"]]
       [:section.contextual-bgs
        [:h3 "Contextual Backgrounds"]
        [:div.bg-primary "Primary"]
        [:div.bg-success "Success"]
        [:div.bg-info "Info"]
        [:div.bg-warning "Warning"]
        [:div.bg-danger "Danger"]]
       [:section.forms-ex
        [:h3 "Forms"]
        [:form {:role "form"}
         [:div.form-group
          [:label {:for "email"} "Email"]
          [:input.form-control {:type "email" :id "email" :placeholder "Enter email"}]]
         [:div.form-group
          [:label {:for "password"} "Password"]
          [:input.form-control {:type "password" :id "password" :placeholder "Password"}]]
         [:div.form-group
          [:div.checkbox
           [:label
            [:input {:type "checkbox"}]
            "Check me out"]]]
         [:button.btn.btn-default {:type "submit"} "Submit"]]]]}
     {:title "Common Elements"
      :nav-target "common-elements"
      :content
      [[:p "Null-state plate, shown where there's nothing of something."
        [:div.example.checker-bg
         [:div.null-state
          "We don't have any of those!"]]]
       [:p "Namespace nav tree, nests namespaces to save on horizontal space. Namespaces are linked, non-namespace bridge parts (e.g. "
        [:code "clojure"]
        ", "
        [:code "java"]
        ") are unlinked."]
       [:div.example.checker-bg
        (common/$namespaces ["clojure.core"
                             "clojure.java.shell"
                             "clojure.test"
                             "clojure.test.junit"
                             "clojure.test.tap"
                             "clojure.zip"])]]}
     (let [sphere '{:title "Simple Values",
                    :categories
                    ({:title "Regular Expressions",
                      :groups
                      ({:syms (re-pattern re-matcher), :title "Create"}
                       {:syms (re-find re-matches re-seq re-groups), :title "Use"})})}]
       {:title "Quick Reference"
        :nav-target "quickref"
        :content
        [[:div.example.checker-bg
          (quickref/$toc [sphere])]
         [:div.example.checker-bg
          (quickref/$sphere sphere)]]})
     {:title "See Alsos"
      :nav-target "see-alsos"
      :content
      [[:p "Null state"]
       [:div.example.checker-bg.sg-see-alsos-null-state]
       [:p "Populated"]
       [:div.example.checker-bg.sg-see-alsos-populated]
       [:p "Add new"]
       [:div.example.checker-bg.sg-add-see-also]]}
     {:title "Notes"
      :nav-target "notes"
      :content
      [[:p "Null state:"]
       [:div.example.checker-bg.sg-notes-null-state]
       [:p "Populated with examples:"]
       [:div.example.checker-bg.sg-notes-populated]
       [:p "Adding a note:"]
       [:div.example.checker-bg.sg-add-note]]}]))

(defn styleguide-handler [{:keys [user uri]}]
  ($tpl
    {:user user
     :page-uri uri
     :nav ($nav "General" styleguide-sections)
     :content
     [:div
      [:h1 "Styleguide"]
      [:p.lead "Here you'll find various UI elements used on the ClojureDocs site. This styleguide is designed to help you see how changes will the vairous states of our UI elements when making changes."]
      (map $section styleguide-sections)]}))

(defn search-styleguide-handler [{:keys [user uri]}]
  ($tpl
    {:user user
     :page-uri uri
     :nav ($nav "Search" search-styleguide-sections)
     :content
     [:div
      [:h1 "Search Stylguide"]
      (map $section search-styleguide-sections)]}))

(defn examples-styleguide-handler [{:keys [user uri]}]
  ($tpl
    {:user user
     :page-uri uri
     :nav ($nav "Examples" examples-styleguide-sections)
     :content
     [:div
      [:h1 "Examples Stylguide"]
      (map $section examples-styleguide-sections)]}))


(defn perf-handler [{:keys [user uri]}]
  ($tpl
    {:user user
     :page-uri uri
     :content
     [:div
      [:h1 "Search Performance"]]}))

(defn canary-tests-handler [{:keys [user uri]}]
  ($tpl
    {:user user
     :page-uri uri
     :content
     [:div [:h1 "Canary"]]}))

(defn format-http-method [k]
  (-> k name str/upper-case))

(defn type-doc [t]
  (or (get {s/Str "String"} t) (pr-str t)))

(defn field-docs [{:keys [field-docs]}]
  [:table.table.field-schemas
   [:tr
    [:th "key"]
    [:th "doc"]]
   (for [[k v] field-docs]
     [:tr
      [:td [:code (str k)]]
      [:td v]])])

(defn schema-key [k]
  (str (if (s/optional-key? k)
         (:k k)
         k)))

(defn schema-table [schema]
  (let [{:keys [name docs]} (meta schema)]
    [:div
     [:h3 name]
     #_[:pre
      (util/pp-str schema)]
     [:table.table.schema
      [:tr
       [:th "name"]
       [:th "type"]
       [:th "notes"]]
      (for [[k v] schema]
        [:tr
         [:td [:code (schema-key k)]]
         [:td (type-doc v)]
         [:td
          (get docs k)
          (when-not (s/optional-key? k)
            "Required")]])]]))

(defn docs-for [{:keys [method path schemas]}]
  [:div
   [:h2 [:code (format-http-method method) " " path]]
   [:h3 "Request"]
   (for [[k v] (:req schemas)]
     [:div
      [:h4 (-> (if (s/optional-key? k) (:k k) k)
               str)]
      (schema-table v)])])

(defn api-docs-handler [{:keys [user uri]}]
  ($tpl
    {:user user
     :page-uri uri
     :content
     [:div
      [:h1 "ClojureDocs API"]
      [:div.markdown
       (->> "src/md/api/overview.md"
            common/memo-markdown-file)]
      [:h2 "Endpoints"]
      (docs-for schemas/get-examples-endpoint)]}))
