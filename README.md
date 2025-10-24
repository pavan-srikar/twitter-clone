# Twitter Clone 
This project is made with Spring Boot and Angular.


# Technologies
java v17 openjdk

Spring Boot 2.7.0

Angular 14

MySql 8.0
# Features

## Home page
User can view all posts stored in database, create new tweet via tweet form, retweet, reply or quote via overlay forms, like and bookmark a tweet.

## Post Page
This page opens when user clicks on a post. Here user can view the post and all replies to it and create a reply via reply form.

## Profile Page
In this page User can update his personal information such as name, bio, location, personal website and birthdate. 
Also, He/She can upload a profile picture and a banner picture. Additionally, user can view his/her tweets, replies, retweets and liked tweets via respective tabs.

## Bookmarks Page
Here user can view his bookmarked tweets.


--------------------------

Alright, here’s the simple “copy-paste and run” guide for your friends:

---

### **Steps for your friends**

1. **Install Docker & Docker Compose**
   Make sure they have the latest Docker Desktop installed (works on Windows, macOS, Linux).

2. **Clone your project**

   ```bash
   git clone <your-repo-url>
   cd twitter-clone
   ```

3. **Go to the folder with `docker-compose.yml`**

   ```bash
   cd docker  # if you put docker-compose.yml here
   ```

4. **Build and start the containers**

   ```bash
   docker-compose up --build
   ```

   * This will **build both backend and frontend** images and start containers.
   * Backend → accessible at [http://localhost:8080](http://localhost:8080)
   * Frontend → accessible at [http://localhost:4200](http://localhost:4200)

5. **Modify code freely**

   * Any changes in `backend` or `frontend` folders on their machine will reflect in the container immediately.
   * Angular auto-reloads; Spring Boot will pick changes if using `spring-boot-devtools`.

6. **Stop the containers**

   ```bash
   docker-compose down
   ```

---
