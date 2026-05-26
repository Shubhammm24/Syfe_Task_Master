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

*   **Auth Controller (`/api/auth`)**
    *   `POST /register`: Register a new user.
    *   `POST /login`: Authenticate a user and get a JWT token.
*   **Category Controller (`/api/categories`)**
    *   `GET /`: Get all categories for the logged-in user.
    *   `POST /`: Create a new category.
*   **Goal Controller (`/api/goals`)**
    *   `GET /`: Get all goals for the logged-in user.
    *   `POST /`: Create a new goal.
*   **Transaction Controller (`/api/transactions`)**
    *   `GET /`: Get all transactions for the logged-in user.
    *   `POST /`: Create a new transaction.
*   **Report Controller (`/api/reports`)**
    *   `GET /summary`: Get a summary report of transactions.

**Note:** This is a sample README file and the endpoints are based on the file names in the `controllers` directory. You may need to adjust the details based on the actual implementation.
