
🎬 Popcorn Palace – Project Instructions
Welcome to the Popcorn Palace Cinema Management System! 🍿
This guide will walk you through setting up and running the application locally.

✅ Prerequisites
Java 17 or higher

Maven

PostgreSQL (local or via Docker)

📦 Project Setup
Clone the repository:

bash
Copy
Edit
git clone https://github.com/amiramir96/popcorn-palace.git
cd popcorn-palace
Install dependencies (optional if you use spring-boot:run):

bash
Copy
Edit
./mvnw clean install
🐘 PostgreSQL Setup
Option A – Using Docker (Recommended):
bash
Copy
Edit
docker run --name popcorn-db -e POSTGRES_DB=popcorn_palace -e POSTGRES_USER=popcorn-palace -e POSTGRES_PASSWORD=popcorn-palace -p 5432:5432 -d postgres
Option B – Manual Installation:
Make sure you have a postgres user with password popcorn-palace

Create a database named popcorn_palace

🚀 Running the Application
bash
Copy
Edit
./mvnw spring-boot:run
The server will start at:
http://localhost:8080

🧪 Running Tests
bash
Copy
Edit
./mvnw test
Includes full end-to-end integration test in PopcornPalaceApplicationTests.

ℹ️ Notes
You can change DB settings in src/main/resources/application.properties or application.yaml

Use valid ISO 8601 timestamps (UTC) for startTime and endTime fields in JSON

Make sure port 5432 is available and pointing to your Docker container (if used)

Make sure port 8080 is available before running the app
