# Banking Transactions API


A Spring Boot application for managing users and their accounts, supporting authentication,
caching, money transfers, and periodic balance updates.

### Run the Application

Copy the project, open it in an IDE (e.g., IntelliJ IDEA), and start the application.
All dependencies (PostgreSQL, Elasticsearch, Redis) will be started automatically using the compose.yaml file.

📚 API documentation is available at: [http://localhost:8000/api/swagger-ui/index.html](http://localhost:8000/api/swagger-ui/index.html)

### 💸 Money Transfer

The `/transfer` endpoint allows an authenticated user to send money to another user.

**Validation includes**:
- Cannot transfer to self
- Amount must be positive
- Sender must have sufficient balance
- Both users must exist

**Concurrency safety**:  
The transfer is executed inside a `@Transactional` method using **pessimistic locking** (`@Lock(PESSIMISTIC_WRITE)`), ensuring thread safety and data consistency under high load. This prevents race conditions and ensures that account balances remain accurate during concurrent transfers.

### 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.4.5**
    - Spring Web
    - Spring Data JPA
    - Spring Security (JWT)
    - Spring Cache (Redis)
    - Spring Quartz
    - Spring Data Elasticsearch
- **PostgreSQL** – database
- **Redis** – caching layer
- **Elasticsearch** – search functionality
- **Liquibase** – database migrations
- **JWT** – authentication
- **OpenAPI (Springdoc)** – API documentation
- **Testcontainers** – integration testing
- **Docker Compose** – service orchestration (PostgreSQL, Redis, Elasticsearch)