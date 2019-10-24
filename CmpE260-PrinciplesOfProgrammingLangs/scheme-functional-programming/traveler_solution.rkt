#lang scheme
;2016400297
;RAILWAY-CONNECTION
(define (RAILWAY-CONNECTION city)                   ;This function calls RAILWAY-CONNECTION-HELPER
  (RAILWAY-CONNECTION-HELPER city 0))                 
(define (RAILWAY-CONNECTION-HELPER city x)          ;Finds the railway connection of given city, if the
  (if (< x (length LOCATIONS))                      ;city and it's connections are exists. Holds a counter
      (if (equal? (car (list-ref LOCATIONS x)) city);to traverse the LOCATIONS list. If it finds the city,
          (list-ref (list-ref LOCATIONS x) 2)       ;returns it's connections. If the counter exceeds the 
          (RAILWAY-CONNECTION-HELPER city (+ x 1))) ;length of LOCATIONS it returns an empty list.
      (list)))                                      
                                                    
;ACCOMMODATION-COST
(define (ACCOMMODATION-COST city)                   ;This function calls ACCOM-COST-HELPER
  (ACCOM-COST-HELPER city 0))
(define (ACCOM-COST-HELPER city x)                  ;Finds the accommodation cost of given city,
  (if (< x (length LOCATIONS))                      ;if the city exists. Holds a counter to tra-
      (if (equal? (car (list-ref LOCATIONS x)) city);verse the LOCATIONS list. If it finds the 
          (list-ref (list-ref LOCATIONS x) 1)       ;city, returns it's cost. If the counter
          (ACCOM-COST-HELPER city (+ x 1)))         ;exceeds the length of LOCATIONS, it returns 0.
      '0))                                          

;INTERESTED-CITIES
(define (INTERESTED-CITIES name)                    ;This function calls the INTERESTED-CITIES-HELPER
  (INTERESTED-CITIES-HELPER name 0))                
(define (INTERESTED-CITIES-HELPER name x)           ;Finds the interested cities of a given person, if the 
  (if (< x (length TRAVELERS))                      ;person exists in database. Holds a counter to traverse
      (if (equal? (car (list-ref TRAVELERS x)) name);the TRAVELERS list. If it finds the person, returns it's
          (list-ref (list-ref TRAVELERS x) 1)       ;interested city list. If the counter exceeds the lenght 
          (INTERESTED-CITIES-HELPER name (+ x 1)))  ;of TRAVELERS, it returns an empty list.
      (list)))

;INTERESTED-ACTIVITIES
(define (INTERESTED-ACTIVITIES name)                  ;This function calls the INTERESTED-ACTIVITIES-HELPER
  (INTERESTED-ACTIVITIES-HELPER name 0))              
(define (INTERESTED-ACTIVITIES-HELPER name x)         ;Finds the interested activities of a given person, if the
  (if (< x (length TRAVELERS))                        ;person exists in database. Holds a counter to traverse
      (if (equal? (car (list-ref TRAVELERS x)) name)  ;the TRAVELERS list. If it finds the person, returns it's
          (list-ref (list-ref TRAVELERS x) 2)         ;interested activity list. If the counter exceeds the lenght
          (INTERESTED-ACTIVITIES-HELPER name (+ x 1)));of TRAVELERS, it returns an empty list.
      (list)))                                        

;HOME
(define (HOME name)                                 ;This function calls the HOME-HELPER
  (HOME-HELPER name 0))
(define (HOME-HELPER name x)                        ;Finds the hometown of a given person, if the person exist in 
  (if (< x (length TRAVELERS))                      ;database. Holds a counter to traverse the TRAVELERS list. If 
      (if (equal? (car (list-ref TRAVELERS x)) name);it finds the given person, it returns person's hometown. If 
          (list-ref (list-ref TRAVELERS x) 3)       ;the counter exceeds the length of TRAVELERS list, it returns
          (HOME-HELPER name (+ x 1)))               ;an empty list, which means no such person in database.
      (list)))

;TRAVELER-FROM
(define (TRAVELER-FROM city)                                                                 ;This function calls the TRAVELER-FROM-HELPER
  (TRAVELER-FROM-HELPER city 0 (list)))
(define (TRAVELER-FROM-HELPER city x ls)                                                     ;Finds the people who live in the given city.
  (if (< x (length TRAVELERS))                                                               ;It holds a counter to traverse the TRAVELERS list.
      (if (equal? (list-ref (list-ref TRAVELERS x) 3) city)                                  ;When it finds a proper person, it appends the person
          (TRAVELER-FROM-HELPER city (+ x 1) (append ls (list (car (list-ref TRAVELERS x)))));to a list. At the end it returns the list of people 
          (TRAVELER-FROM-HELPER city (+ x 1) ls))                                            ;who live in the given city.
      (if (empty? ls) (list) ls)))                                                           

;INTERESTED-IN-CITY
(define (INTERESTED-IN-CITY city)                                                           ;This function calls the INTERESTED-IN-CITY-HELPER
  (INTERESTED-IN-CITY-HELPER city 0 (list)))
(define (INTERESTED-IN-CITY-HELPER city x ls)                                               ;Finds the list of people who want to visit the given
  (if (< x (length TRAVELERS))                                                              ;city. Holds a counter to traverse the TRAVELERS list.
      (if (equal? (contains (list-ref (list-ref TRAVELERS x) 1) city) #t)                   ;When it finds a proper traveler, it adds the person to
          (INTERESTED-IN-CITY-HELPER city (+ x 1)                                           ;a list. At the end it returns the list of people who 
                                     (append ls (list (list-ref (list-ref TRAVELERS x) 0))));want to visit the given city.
          (INTERESTED-IN-CITY-HELPER city (+ x 1) ls))                                      
      (if (empty? ls) (list) ls)))

;INTERESTED-IN-ACTIVITY
(define (INTERESTED-IN-ACTIVITY act)                                                            ;This function calls the INTERESTED-IN-ACTIVITY-HELPER
  (INTERESTED-IN-ACTIVITY-HELPER act 0 (list)))
(define (INTERESTED-IN-ACTIVITY-HELPER act x ls)                                                ;Finds the list of people who are interested in the given
  (if (< x (length TRAVELERS))                                                                  ;activity. Holds a counter to traverse the TRAVELERS list.
      (if (equal? (contains (list-ref (list-ref TRAVELERS x) 2) act) #t)                        ;When it finds a proper traveler, it adds the person to a list.
          (INTERESTED-IN-ACTIVITY-HELPER act (+ x 1)                                            ;It uses the contains helper function to check if the given acti-
                                         (append ls (list (list-ref (list-ref TRAVELERS x) 0))));vity appears in the person's activity list. At the end it 
          (INTERESTED-IN-ACTIVITY-HELPER act (+ x 1) ls))                                       ;returns the list of people who are interested in the given
      (if (empty? ls) (list) ls)))                                                              ;activity.

;RAILWAY-NETWORK
(define (RAILWAY-NETWORK city)                      ;This function calls the RAILWAY-NETWORK-HELPER
  (RAILWAY-NETWORK-HELPER city 0))
(define (RAILWAY-NETWORK-HELPER city x)             ;Checks if the given city exist in databse. If it exist,
  (if (< x (length LOCATIONS))                      ;finds it's RAILWAY-NETWORK, else, returns an empty list.
      (if (equal? (car (list-ref LOCATIONS x)) city);It uses QUEUE helper function to traverse the railway
          (cdr (QUEUE (list city) (list city)))     ;network recursively.
          (RAILWAY-NETWORK-HELPER city (+ x 1)))    
      (list)))
;-----------------------------------------------------------
;HELPER FUNCTIONS FOR RAILWAY-NETWORK
(define (QUEUE qlist alist)                                            ;These two functions recursively call each other
  (if (< 0 (length qlist))                                             ;to traverse the given city's railway network. BFS
      (add-two-list (cdr qlist) alist (RAILWAY-CONNECTION (car qlist)));algorithm is used to traverse the network. add-two-list
      alist))                                                          ;function takes qlist alist and the main city's railway connections
                                                                       ;and recursively traverse each city and city's railway conn. until 
(define (add-two-list qlist alist rconn)                               ;the length of qlist becomes zero. 
  (if (< 0 (length rconn))                                             
      (if (contains alist (car rconn))                                 
          (add-two-list qlist alist (cdr rconn))                       
          (add-two-list (append qlist (list (car rconn)))
                        (append alist (list (car rconn))) (cdr rconn)))
      (QUEUE qlist alist )))
;-----------------------------------------------------------
;ACCOMMODATION-EXPENSES
(define (ACCOMMODATION-EXPENSES name city)                                                    ;This function calls the ACCOMMODATION-EXPENSES-HELPER
  (ACCOMMODATION-EXPENSES-HELPER name city 0))
(define (ACCOMMODATION-EXPENSES-HELPER name city x)                                           ;Finds the accommodation expense of a given city for a given
  (if (< x (length TRAVELERS))                                                                ;person. If the given city is person's hometown, it returns 0.
      (if (equal? (car (list-ref TRAVELERS x)) name)                                          ;Else, it uses common-element helper function to check if the
          (if (equal? (HOME name) city) '0                                                    ;city's activity list and the person's interested activities 
              (if (equal? (common-element (act-of-city city) (INTERESTED-ACTIVITIES name)) #t);have a common element. If they have it returns the three times
                  (* 3 (ACCOMMODATION-COST city))                                             ;of accommodation cost. Else, it returns the accommodation cost
                  (ACCOMMODATION-COST city)))                                                 ;just for one night. It also uses act-of-city helper function to
          (ACCOMMODATION-EXPENSES-HELPER name city (+ x 1)))                                  ;find the activities that can be done in the given city.
      (list)))

;TRAVEL-EXPENSES
(define (TRAVEL-EXPENSES name city)                                                    ;This function calls the TRAVEL-EXPENSES-HELPER
  (TRAVEL-EXPENSES-HELPER name city 0))
(define (TRAVEL-EXPENSES-HELPER name city x)                                           ;Finds the travell expense of the given city for the given person.
  (if (< x (length TRAVELERS))                                                         ;It finds the person by using a counter. After finding the person, it
      (if (equal? (car (list-ref TRAVELERS x)) name)                                   ;checks if the given city occurs in the railway network of the person's
          (if (equal? (HOME name) city) '0                                             ;hometown. If it occurs, it returns 100. Else, it returns 200. It uses 
              (if (equal? (contains (RAILWAY-NETWORK (HOME name)) city) #t) '100 '200));contains helper function to check if the railway network contains the 
          (TRAVEL-EXPENSES-HELPER name city (+ x 1)))                                  ;the given city.
      (list)))

;EXPENSES
(define (EXPENSES name city)                                         ;This function finds the total expenses to travel to given city
  (+ (ACCOMMODATION-EXPENSES name city) (TRAVEL-EXPENSES name city)));for a person. It just adds the other expenses functions' results.

;IN-BETWEEN
(define (IN-BETWEEN sml big)                                                                 ;This function calls IN-BETWEEN-HELPER
  (IN-BETWEEN-HELPER sml big 0 (list)))
(define (IN-BETWEEN-HELPER sml big x ls)                                                     ;Finds the cities which accommodation costs are
  (if (< x (length LOCATIONS))                                                               ;between the given limits. It uses a counter to traverse
      (if (and (<= (list-ref (list-ref LOCATIONS x) 1) big)                                  ;the LOCATIONS list. When it finds a proper city, it appends
               (<= sml (list-ref (list-ref LOCATIONS x) 1)))                                 ;it to a list. In the end it returns the final list which 
          (IN-BETWEEN-HELPER sml big (+ x 1) (append ls (list (car (list-ref LOCATIONS x)))));contains cities that have proper accommodation costs.
          (IN-BETWEEN-HELPER sml big (+ x 1) ls))                                            
      (if (empty? ls) (list) ls)))                                                           

;---------------HELPER FUNCTIONS---------------

;ACTIVITIES-OF-A-CITY
(define (act-of-city city)                           ;This function finds the activities that
  (ACTIVITIES-OF-A-CITY-HELPER city 0 ))             ;can be done in a city. It just takes the 
(define (ACTIVITIES-OF-A-CITY-HELPER city x)         ;city's name as a parameter and traverse the 
  (if (< x (length LOCATIONS))                       ;LOCATIONS list by using a counter to find the
      (if (equal? (car (list-ref LOCATIONS x)) city) ;given city. After it finds the city, it returns
          (list-ref (list-ref LOCATIONS x) 3)        ;the activities of that city.
          (ACTIVITIES-OF-A-CITY-HELPER city (+ x 1)))
      (list)))

;COMMON-ELEMENT
(define (common-element ls1 ls2)           ;This function checks if two lists contains
  (if (empty? ls1) #f                      ;at least one common element. It basically compare every
      (if (contains ls2 (car ls1)) #t      ;element of list1 with list2 by using contains helper 
          (common-element (cdr ls1) ls2))));function.

;CONTAINS
(define (contains list element)           ;This function checks if the given element occurs
  (if (empty? list) #f                    ;in the given list. It basically compare the given
      (if (equal? (car list) element) #t  ;element with the list's first item until it finds
          (contains (cdr list) element))));the element or the list becomes empty.