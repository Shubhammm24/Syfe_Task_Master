# Syfe Task Manager

This is a simple task manager application built with Spring Boot, Kotlin, and JPA.

## Technologies Used

- **Backend:** Spring Boot, Kotlin
- **Database:** H2 (In-memory)
- **Authentication:** Spring Security
- **Build Tool:** Gradle

## How to Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/syfe-task-master.git
    ```
2.  **Navigate to the project directory:**
    ```bash
    cd syfe-task-master
    ```
3.  **Build the project:**
    ```bash
    ./gradlew build
    ```
4.  **Run the application:**
    ```bash
    ./gradlew bootRun
    ```
The application will be available at `http://localhost:8080`.

## API Endpoints

The following are the major API endpoints. For detailed information, please refer to the controller classes.

### 1. Register a new user

```bash
curl -i -X POST https://syfe-task.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"shubham@example.com","password":"password123","fullName":"Shubham","phoneNumber":"1234567890"}'

```

### 2. Log in (saves the session cookie in `cookies.txt`)

```bash
curl -i -X POST https://syfe-task.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"shubham@example.com","password":"password123"}' \
  -c cookies.txt

```

### 3. Fetch default + custom categories (reads the cookie from `cookies.txt`)

```bash
curl -i -X GET https://syfe-task.onrender.com/api/categories -b cookies.txt

```

### 4. Add a custom category

```bash
curl -i -X POST https://syfe-task.onrender.com/api/categories \
  -b cookies.txt \
  -H "Content-Type: application/json" \
  -d '{"name":"Bonus","type":"INCOME"}'

```

### 5. Add a transaction

```bash
curl -i -X POST https://syfe-task.onrender.com/api/transactions \
  -b cookies.txt \
  -H "Content-Type: application/json" \
  -d '{"amount":5000.00,"date":"2026-05-23","categoryName":"Salary","description":"May Salary Payout"}'

```

### 6. Create a Savings Goal

```bash
curl -i -X POST https://syfe-task.onrender.com/api/goals \
  -b cookies.txt \
  -H "Content-Type: application/json" \
  -d '{"goalName":"MacBook","targetAmount":2000.00,"targetDate":"2026-12-31"}'

```

### 7. Fetch Savings Goal & Live Progress

```bash
curl -i -X GET https://syfe-task.onrender.com/api/goals -b cookies.txt

```

### 8. Generate a Monthly Report

```bash
curl -i -X GET https://syfe-task.onrender.com/api/reports/monthly/2026/5 -b cookies.txt

```
