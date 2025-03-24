# 🎬 Popcorn Palace – Project Instructions

Welcome to the Popcorn Palace Cinema Management System! 🍿  
This guide will walk you through setting up and running the application locally.

---

## ✅ Prerequisites

- Java 21 or higher: https://www.oracle.com/java/technologies/downloads/#java21
- Java IDE:  https://www.jetbrains.com/idea/download
- Maven: https://maven.apache.org/download.cgi
- PostgreSQL (local): https://www.postgresql.org/download/windows/

---

## 📦 Project Setup

1. **Clone the repository:**
   ```Terminal:
   git clone https://github.com/amiramir96/popcorn-palace.git
   ```

2. **Install dependencies:**
   ```Terminal:
   ./mvnw clean install
   ```
---
## 🐘 PostgreSQL Setup

### Installation:
- Make sure you have a `postgres` user with password `popcorn-palace`.
- I used the default setting of the assignment. username: `popcorn-palace`, password: `popcorn-palace`.
- Create a database named `popcorn_palace`.
- Note: i did not used docker and run my application and db locally so i did not added installation instructions for loading db for docker.
---

## 🚀 Running the Application

```Terminal:
./mvnw spring-boot:run
```

The server will start at:  
`http://localhost:8080`

---

## 🧪 Running Tests

To run all tests use
```bash
./mvnw test
```
Used MockMvc for the integration tests.
The `PopcornPalaceApplicationTests` includes full scenario of integration test (more details in the file documentation).
The other test files are separates to unit tests over the Services. Integration tests for the Controllers.

I used also Postman application for manual tests along my work.
can download it here: https://www.postman.com/downloads/

---

## ℹ️ Notes

- You can change DB settings in `src/main/resources/application.yaml`
- Use valid ISO 8601 timestamps (UTC) for `startTime` and `endTime` fields in JSON for requests of `/showtimes`.
- Use valid UUID for BookingId fields in JSON for POST requests of `/bookings`.
- Make sure port 5432 is available and pointing to your Docker container (if used)
- Make sure port 8080 is available before running the app

---

## Assumptions & Design Decisions
- No null fields are allowed in the data of requests.
- POST requests that represent 'update' command have to hold the data for the whole object and not partial data.
- When DELETE request gained, for example for Movie entity. all the showtime that points to this movie are deletion as well and also all the bookings that points to the deleted showtime (to avoid from orphan data).
- All requests are Transactional which means there is not partial add, delete or update even if the program explodes in the middle of the running.
- UserId of Booking entity is String. Couldnt decide if its must be UUID or any string based on one example.
