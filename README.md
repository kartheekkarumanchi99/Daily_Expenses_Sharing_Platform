💸 Daily Expenses Sharing Application
A robust backend RESTful application built with Spring Boot and MySQL designed to manage daily expenses, divide balances through custom split algorithms, and generate accurate balance sheets.
📌 Overview
Managing group expenses can quickly get complicated. The Daily Expenses Sharing Application simplifies group finance by providing an automated system to manage users, track expenses, calculate splits (equal, exact, or percentage-based), and generate summary balance sheets.
✨ Features
👤 User Management: Create, manage, and retrieve profile details for app users.
💳 Expense Tracking: Record individual or shared expenses effortlessly.
⚖️ Flexible Expense Splitting:
Equal: Split costs evenly among all participants.
Exact: Specify precise dollar/currency amounts for each participant.
Percentage: Divide expenses based on custom percentage allocations.
📊 Balance Sheet Generation: View consolidated summaries showing total amounts owed and spent per user.
🛠️ Tech Stack
Framework: Spring Boot 3.3.2
Persistence Layer: Spring Data JPA / Hibernate
Database: MySQL 8.0+
Validation: Jakarta Validation API
Boilerplate Reduction: Project Lombok
🏗️ Project Structure
Plaintext
src/main/java/com/expensesharing/
├── controllers/          # REST Endpoints (UserController, ExpenseController)
├── services/             # Core Business Logic (UserService, ExpenseService)
├── repositories/         # Spring Data JPA Data Access Interfaces
├── entities/             # JPA Entities (User, Expense, Participant, SplitType)
└── utilities/            # Helper Utilities (ExpenseSplitUtil)
⚙️ Database Configuration
The application uses Hibernate ORM to automatically manage schema creation and updates.
Setup Instructions
Install and start your local MySQL Server.
Create a new database schema named expensesharingdb:
SQL
CREATE DATABASE expensesharingdb;
Update your database credentials in src/main/resources/application.properties:
Properties
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
🚀 Installation & Setup
Prerequisites
JDK 17 or higher
Maven 3.6+
MySQL 8.0+
Steps
Clone the repository:
Bash
git clone https://github.com/kartheekkarumanchi99/Daily_Expenses_Sharing_Platform.git
cd Daily_Expenses_Sharing_Platform
Configure Database:
Ensure application.properties reflects your local MySQL credentials.
Build & Run Application:
Bash
# Using Maven
./mvnw spring-boot:run
Alternatively, open the project in IntelliJ IDEA or Eclipse and run the main application class.
Verify Application Status:
The server will start at http://localhost:8080.
📡 API Reference
👤 User Endpoints
Method	Endpoint	Description
POST	/users	Create a new user
GET	/users/{id}	Get user details by ID
GET	/users	Fetch all registered users
Sample Request Body (POST /users):
JSON
{
  "name": "user1",
  "email": "test1@gmail.com",
  "mobile": "1234567890"
}
💵 Expense Endpoints
Method	Endpoint	Description
POST	/expenses	Record a new expense
GET	/expenses/{id}	Get specific expense by ID
GET	/expenses	Retrieve all expenses
GET	/expenses/user/{userId}	Get expenses linked to a specific user
GET	/expenses/balance-sheet	Download balance sheet
📸 Snapshots
📬 Contact
For any inquiries or feedback, please contact:
Email: kartheekkarumanchi99@gmail.com
GitHub: @kartheekkarumanchi99