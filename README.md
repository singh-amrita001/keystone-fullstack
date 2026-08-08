# Keystone Project

A full-stack Work Order Management System built using Spring Boot, React, MySQL, and Docker.

## 📁 Project Structure

```
Keystone-Project
│
├── keystone-backend
│   ├── src
│   ├── pom.xml
│   └── Dockerfile
│
├── keystone-frontend
│   ├── src
│   ├── package.json
│   └── Dockerfile
│
├── database
│   └── init.sql
│
├── docker-compose.yml
│
└── README.md
```

---

## 🚀 Technologies Used

### Backend

- Java 24
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Flyway
- JWT Authentication
- Swagger

### Frontend

- React.js
- Axios
- React Router
- React Icons
- CSS

### Database

- MySQL 8

### Tools

- Docker
- Git
- GitHub
- Eclipse
- MySQL Workbench

---

## ⚙️ Backend Setup

Go to the backend folder:

```bash
cd keystone-backend
```

Run the application:

```bash
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8082
```

Swagger:

```text
http://localhost:8082/swagger-ui/index.html
```

---

## ⚛️ Frontend Setup

Go to the frontend folder:

```bash
cd keystone-frontend
```

Install dependencies:

```bash
npm install
```

Start the React application:

```bash
npm start
```

Frontend URL:

```text
http://localhost:3000
```

---

## 🐳 Run with Docker

Start all containers:

```bash
docker compose up --build
```

Stop containers:

```bash
docker compose down
```

Check running containers:

```bash
docker ps
```

---

## 🗄️ Database

Database name:

```text
keystone
```

MySQL port:

```text
3307
```

Username:

```text
root
```

Password:

```text
root123
```

---

## 🔐 Features

- User authentication with JWT
- Role-based authorization
- Work order management
- Dashboard
- User management
- File upload support
- Swagger API documentation

---

## 👩‍💻 Author

**Amrita Singh**

GitHub:

https://github.com/singh-amrita001