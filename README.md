# 💸 Daily Expenses Sharing Platform

> A robust backend RESTful application built with **Spring Boot and MySQL** designed to manage daily expenses, divide balances through custom split algorithms, and generate accurate balance sheets.

---

## 📌 Overview

Managing group expenses can quickly become complicated. The **Daily Expenses Sharing Platform** simplifies group finance by providing an automated system to manage users, track expenses, calculate splits, and generate summary balance sheets.

The application supports multiple expense-splitting strategies, allowing expenses to be divided based on **equal amounts, exact amounts, or custom percentages**.

---

## ✨ Features

### 👤 User Management

Create, manage, and retrieve profile details for application users.

### 💳 Expense Tracking

Record individual or shared expenses and retrieve expense information whenever required.

### ⚖️ Flexible Expense Splitting

The application supports three different ways of splitting expenses:

* **Equal** — Split the expense evenly among all participants.
* **Exact** — Specify the exact amount each participant should contribute.
* **Percentage** — Divide the expense based on custom percentage allocations.

### 📊 Balance Sheet Generation

Generate consolidated balance sheets showing the amounts spent and owed by users.

---

## 🛠️ Tech Stack

| Technology             | Purpose                         |
| ---------------------- | ------------------------------- |
| **Java 17+**           | Backend development             |
| **Spring Boot 3.3.2**  | RESTful application framework   |
| **Spring Data JPA**    | Data persistence                |
| **Hibernate**          | ORM                             |
| **MySQL 8.0+**         | Relational database             |
| **Jakarta Validation** | Input validation                |
| **Project Lombok**     | Boilerplate reduction           |
| **Maven**              | Build and dependency management |

---

## 🏗️ Project Structure

```text
src/main/java/com/expensesharing/

├── controllers/
│   ├── UserController.java
│   └── ExpenseController.java
│
├── services/
│   ├── UserService.java
│   └── ExpenseService.java
│
├── repositories/
│   └── Spring Data JPA repositories
│
├── entities/
│   ├── User.java
│   ├── Expense.java
│   ├── Participant.java
│   └── SplitType.java
│
└── utilities/
    └── ExpenseSplitUtil.java
```

### Architecture Responsibilities

| Layer            | Responsibility                          |
| ---------------- | --------------------------------------- |
| **Controllers**  | Handle REST API requests                |
| **Services**     | Implement core business logic           |
| **Repositories** | Handle database access                  |
| **Entities**     | Represent application data models       |
| **Utilities**    | Handle reusable expense-splitting logic |

---

## ⚖️ Expense Splitting

The application provides three different split algorithms.

### Equal Split

The total expense is divided equally among all participants.

```text
Total Expense: ₹1,000
Participants: 4

Each participant → ₹250
```

### Exact Split

A specific amount can be assigned to every participant.

```text
Total Expense: ₹1,000

User A → ₹400
User B → ₹300
User C → ₹200
User D → ₹100
```

### Percentage Split

The expense can be distributed according to custom percentages.

```text
Total Expense: ₹1,000

User A → 40% → ₹400
User B → 30% → ₹300
User C → 20% → ₹200
User D → 10% → ₹100
```

---

## 📊 Balance Sheet

The balance-sheet functionality provides a consolidated view of expenses and participant balances.

It helps determine:

* Total expenses
* Individual contributions
* Amounts owed by participants
* Overall user balances

---

## 🗄️ Database Configuration

The application uses **MySQL 8.0+** for persistent data storage.

Create the database:

```sql
CREATE DATABASE expensesharingdb;
```

Update the database credentials in:

```text
src/main/resources/application.properties
```

Example configuration:

```properties
# MySQL Database Connection Configuration

spring.datasource.url=jdbc:mysql://localhost:3306/expensesharingdb
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver

# JPA & Hibernate Settings

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

> ⚠️ **Note:** Do not commit real database credentials to the repository.

---

# 🚀 Installation & Setup

## Prerequisites

Make sure the following are installed:

* **JDK 17 or higher**
* **Maven 3.6+**
* **MySQL 8.0+**
* **Git**

---

## 1. Clone the Repository

```bash
git clone https://github.com/kartheekkarumanchi99/Daily_Expenses_Sharing_Platform.git

cd Daily_Expenses_Sharing_Platform
```

---

## 2. Configure MySQL

Start your local MySQL server and create the database:

```sql
CREATE DATABASE expensesharingdb;
```

---

## 3. Configure Application Properties

Open:

```text
src/main/resources/application.properties
```

and configure your MySQL credentials:

```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

---

## 4. Build & Run

Using Maven:

```bash
./mvnw spring-boot:run
```

Alternatively, open the project in **IntelliJ IDEA**, **Eclipse**, or **VS Code** and run the main Spring Boot application class.

---

## 5. Verify Application

Once the application starts successfully, the server will be available at:

```text
http://localhost:8080
```

The APIs can be tested using Postman, cURL, or any compatible REST client.

---

# 📡 API Reference

## 👤 User Endpoints

| Method | Endpoint      | Description                |
| ------ | ------------- | -------------------------- |
| `POST` | `/users`      | Create a new user          |
| `GET`  | `/users/{id}` | Get user details by ID     |
| `GET`  | `/users`      | Fetch all registered users |

### Create User

```http
POST /users
```

Example request:

```json
{
  "name": "user1",
  "email": "test1@gmail.com",
  "mobile": "1234567890"
}
```

---

## 💵 Expense Endpoints

| Method | Endpoint                  | Description                   |
| ------ | ------------------------- | ----------------------------- |
| `POST` | `/expenses`               | Record a new expense          |
| `GET`  | `/expenses/{id}`          | Get a specific expense        |
| `GET`  | `/expenses`               | Retrieve all expenses         |
| `GET`  | `/expenses/user/{userId}` | Get expenses linked to a user |
| `GET`  | `/expenses/balance-sheet` | Generate balance sheet        |

---

# 📸 Proof of Work

> The following snapshots demonstrate the implemented application and provide visual proof of the project's development and functionality.

<p align="center">
  <img src="./img_1.png" alt="Project Snapshot 1" width="800"/>
</p>

<p align="center">
  <img src="./img_2.png" alt="Project Snapshot 2" width="800"/>
</p>

<p align="center">
  <img src="./img_3.png" alt="Project Snapshot 3" width="800"/>
</p>

<p align="center">
  <img src="./img_4.png" alt="Project Snapshot 4" width="800"/>
</p>

<p align="center">
  <img src="./img_5.png" alt="Project Snapshot 5" width="800"/>
</p>

<p align="center">
  <img src="./img_6.png" alt="Project Snapshot 6" width="800"/>
</p>

<p align="center">
  <img src="./img_7.png" alt="Project Snapshot 7" width="800"/>
</p>

<p align="center">
  <img src="./img_8.png" alt="Project Snapshot 8" width="800"/>
</p>

<p align="center">
  <img src="./img_9.png" alt="Project Snapshot 9" width="800"/>
</p>

<p align="center">
  <img src="./img_10.png" alt="Project Snapshot 10" width="800"/>
</p>

<p align="center">
  <img src="./img_11.png" alt="Project Snapshot 11" width="800"/>
</p>

<p align="center">
  <img src="./img_12.png" alt="Project Snapshot 12" width="800"/>
</p>

---

# 🔍 Engineering Highlights

* **Layered Architecture** separating controllers, services, repositories, entities, and utilities.
* **Multiple Expense Split Algorithms** supporting equal, exact, and percentage-based splitting.
* **RESTful API Design** for managing users and expenses.
* **JPA/Hibernate Persistence** for reliable database interaction.
* **MySQL Integration** for persistent relational data storage.
* **Automated Balance Calculation** for generating consolidated expense summaries.
* **Input Validation** using Jakarta Validation.
* **Maintainable Business Logic** with expense calculation isolated from REST controllers.

---

# 🗺️ Future Improvements

Potential extensions to the platform include:

* 🔐 Authentication & Authorization
* 👥 Group Expense Management
* 💰 Debt Simplification
* 📱 Frontend Application
* 📧 Expense Notifications
* 📊 Expense Analytics
* 🧪 Automated Unit & Integration Testing
* 📖 Swagger / OpenAPI Documentation
* 🐳 Docker Containerization
* ☁️ Cloud Deployment
* ⚡ Caching and Performance Optimization

---

# 📂 Repository Structure

```text
Daily_Expenses_Sharing_Platform/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/expensesharing/
│   │   │       ├── controllers/
│   │   │       ├── services/
│   │   │       ├── repositories/
│   │   │       ├── entities/
│   │   │       └── utilities/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│
├── img_1.png
├── img_2.png
├── img_3.png
├── img_4.png
├── img_5.png
├── img_6.png
├── img_7.png
├── img_8.png
├── img_9.png
├── img_10.png
├── img_11.png
├── img_12.png
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

# 📬 Contact

For any inquiries, feedback, or collaboration:

**Kartheek Karumanchi**

📧 Email: **[kartheekkarumanchi99@gmail.com](mailto:kartheekkarumanchi99@gmail.com)**

🐙 GitHub: **@kartheekkarumanchi99**

---

<div align="center">

### ⭐ If you find this project useful, consider giving the repository a star!

**Built with ☕ Java • 🌱 Spring Boot • 🐬 MySQL**

</div>
