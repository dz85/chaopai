(ns chaopai.routes.home
  (:use compojure.core)
  (:require [chaopai.views.layout :as layout]
            [chaopai.models.db :as db] 
            [clabango.filters :refer [deftemplatefilter]]
            [clabango.parser :refer [render]]
            [noir.response :as response]
            [noir.io :as io]
            [cheshire.core :refer :all]
            [chaopai.util :as util]))
(use 'korma.core)
(use '[pghstore-clj.core])




(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")})
  )

(defn get-about-me []
  (first
    (select db/kv 
      (where {:key "about-me"})
        (limit 1))))


(defn about-page [page]
  (layout/render page {:about-me (:value (get-about-me))}))

(defn link-me []
  (layout/render "link-me.html" {:link-info (:value (first
          (select db/kv 
            (where {:key "link-me-info"})
            (limit 1))))}))

(defn save-linke-me [k v]
  (exec-raw ["update kv set value = ? where key = ? " [v k]])
  (if (= k "about-me")
    (response/redirect "/admin/about-me")
    (response/redirect "/admin/link-me")))

(defn check-admin [id pwd]
  (if (and
        (= id "admin")
        (= pwd "admin"))
      true
      false))
  
(defn init-p-data []
  (doseq [p (select db/product)]
    (insert db/productpic 
      (values [{:uuid "001.jpg" :productid (:id p)}
               {:uuid "002.jpg" :productid (:id p)}
               {:uuid "003.jpg" :productid (:id p)}]))))

(defn admin-link-me []
  (layout/render "admin-link-me.html"
    {:link-me-info  (:value (first
          (select db/kv 
            (where {:key "link-me-info"})
            (limit 1)))) 
       }))

(defn check-admin-action [id pwd]
  (if (check-admin id pwd)
    (admin-link-me)
    (layout/render "admin-chaopai.html" {:error "密码错误"})))

(defn handle-upload [file uuid]
  (io/upload-file "/upload" (assoc file :filename uuid))
  (response/json {
        "error"  0,
        "url"  (str "/upload?id=" uuid)
  }))
(defn handle-upload-page [title file id uuid url menu]
  (println uuid)
  (insert db/productpic 
    (values [{:title title :uuid uuid
              :productid (read-string id)}]))
  (io/upload-file "/upload" (assoc file :filename uuid))
  (response/redirect (str url "?id=" id "&menu=" menu)))

(defn get-product [types cat]
  (map-indexed #(assoc %2 :index (if (= %1 0) 
    0
    (+ (* 45 %1) (rand-int 40))))
    (exec-raw ["SELECT 
  DISTINCT ON (product.id)
  productpic.id as pid,
  product.id, 
  product.name, 
  product.intro
FROM 
  product  INNER JOIN productpic on product.id = productpic.productid where product.types = ? and product.cat = ? 
" [types cat]] :results)))

(defn get-product-style [types cat]
  (let [p-count  
    (count (get-product 
      types cat))]
    (if (> p-count 10)
      393
      393)))

(defn product []
  (layout/render "product.html" {:curtain (get-product "product" "窗簾")
                                 :fabire  (get-product "product" "布藝")
                                 :wallpaper  (get-product "product" "墻紙")
                                 :carpet  (get-product "product" "地毯")
                                 :crafts  (get-product "product" "工藝品")
                                 :furniture  (get-product "product" "家具")
                                 :curtain-l (get-product-style "product" "窗簾")
                                 :fabire-l  (get-product-style "product" "布藝")
                                 :wallpaper-l  (get-product-style "product" "墻紙")
                                 :carpet-l  (get-product-style "product" "地毯")
                                 :crafts-l  (get-product-style "product" "工藝品")
                                 :furniture-l  (get-product-style "product" "家具")}))

(defn service []
  (layout/render "service.html" {:hotel (get-product "service" "酒店")
                                 :villa  (get-product "service" "別墅")
                                 :officespace  (get-product "service" "辦公空間")
                                 :clubhouse  (get-product "service" "會所")
                                 :hotel-l (get-product-style "service" "酒店")
                                 :villa-l  (get-product-style "service" "別墅")
                                 :officespace-l  (get-product-style "service" "辦公空間")
                                 :clubhouse-l  (get-product-style "service" "會所")}))

(defn admin-product []
  (layout/render "admin-product.html" {:list 
    (select db/product 
      (where {:types "product"}))}))

(defn admin-service []
  (layout/render "admin-service.html" {:list 
    (select db/product 
      (where {:types "service"}))}))

(defn get-uuid [] 
  (str (java.util.UUID/randomUUID)))

(defn del-product [id url] 
  (delete db/product
    (where {:id (read-string id)}))
  (response/redirect url))

(defn del-product-pic [id url] 
  (delete db/productpic
    (where {:id (read-string id)}))
  (response/redirect url))

(defn detail-product [id menu html] 
  (layout/render html  {:menu menu 
                        :list  (map-indexed #(assoc %2 :index (+ %1 1)) (select db/productpic 
            (where {:productid (read-string id)})))
                        :id id}))

(defn add-product-pic [id menu] 
  (layout/render "add-product-pic.html" {:menu menu 
                                        :id id}))



(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about-me" [] (about-page "about-me.html"))
  (GET "/link-me" [] (link-me))
  (GET "/test" [] "help" )
  (GET "/product" [] (product))
  (GET "/init-p-data" [] (init-p-data))
  (GET "/service" [] (service))
  (POST "/admin/edit-product" [name types intro cat id] 
    (update db/product
      (set-fields {:name name
               :types types
               :intro intro
               :cat cat})
      (where {:id (read-string id)}))
    
      (if (= types "product")
        (admin-product)
        (admin-service)))
  (POST "/admin/add-product" [name types intro cat] 
    (exec-raw ["insert into product (name, types, intro, cat) values(?, ?, ?, ?)"
     [name types intro cat]])
    (if (= types "product")
      (admin-product)
      (admin-service)))
  (POST "/admin/login" [id pwd] (check-admin-action id pwd))
  (GET "/admin-chaopai" [] (layout/render "admin-chaopai.html"))
  (GET "/admin/link-me" [] (admin-link-me)) 
  (GET "/admin/about-me" [] (about-page "admin-about-me.html")) 
  (GET "/admin/product" [] (admin-product))
  (GET "/admin/service" [] (admin-service))
  (GET "/admin/edit-product" [id menu] (layout/render (str "edit-" menu ".html") {:menu menu :p (first (select db/product (where {:id (read-string id)})))}))
  (GET "/admin/add-product" [] (layout/render "add-product.html"))
  (GET "/admin/add-service" [] (layout/render "add-service.html"))
  (POST "/admin/save-linke-me" [k v] (save-linke-me k v))
  (GET "/admin/detail-product" [id menu] (detail-product id menu "detail-product.html"))
  (GET "/detail" [id menu] (detail-product id menu "product-detail.html"))
  (GET "/admin/add-product-pic" [id menu] (add-product-pic id menu))
  (GET "/admin/del-product" [id url] (del-product id url))
  (GET "/admin/del-product-pic" [id url] (del-product-pic id url))
  (GET "/upload" [id] (io/get-resource (str "/upload/" id)))
  (POST "/upload" [imgFile] (handle-upload imgFile (get-uuid)))
  (POST "/admin/add-product-pic" [title imgFile id menu] 
    (handle-upload-page  title imgFile id 
                        (get-uuid) (str "/admin/detail-" "product" ) menu)))
