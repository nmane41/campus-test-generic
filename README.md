# Online Campus Placement Test System

A complete, production-ready web application for conducting online campus placement tests with role-based access control (Admin and Student).

## Features

### Student Features
- User registration with username, email, and password
- User login using email + password
- Take a placement test (50 MCQ questions)
- Questions are shuffled per user
- View test results after submission
- Automatic score calculation

### Admin Features
- Admin dashboard with statistics:
  - Total registered users
  - Currently active users
  - Total test attempts
  - Average score
- Question Management (CRUD operations)
- View all students' results
- Reset user passwords (generates temporary password)
- Enable/Disable users

## Tech Stack

### Backend
- Java 8+
- Spring Boot 2.7.14
- Spring Security (JWT Authentication)
- Spring Data JPA
- MySQL 8.0
- Maven

### Frontend
- React 18.2
- React Router 6.8
- Axios
- CSS3

### Deployment
- Docker
- Docker Compose
- Nginx (for frontend)

## Project Structure

```
.
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/campusplacement/
│   │       │   ├── config/          # Security & Data initialization
│   │       │   ├── controller/      # REST Controllers
│   │       │   ├── dto/             # Data Transfer Objects
│   │       │   ├── entity/          # JPA Entities
│   │       │   ├── repository/      # JPA Repositories
│   │       │   ├── security/        # JWT Filter
│   │       │   ├── service/         # Business Logic
│   │       │   └── util/            # JWT Utility
│   │       └── resources/
│   │           └── application.properties
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/      # Reusable components
│   │   ├── context/         # Auth context
│   │   ├── pages/           # Page components
│   │   ├── services/        # API services
│   │   ├── App.js
│   │   └── index.js
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml
└── README.md
```

## Database Schema

### Tables

1. **users**
   - id (Primary Key)
   - username (Unique)
   - email (Unique)
   - password_hash (BCrypt hashed)
   - role (ADMIN/USER)
   - enabled (Boolean)
   - created_at

2. **questions**
   - id (Primary Key)
   - question_text
   - option_a
   - option_b
   - option_c
   - option_d
   - correct_option (A/B/C/D)

3. **test_attempts**
   - id (Primary Key)
   - user_id (Foreign Key → users.id)
   - score
   - start_time
   - end_time

4. **answers**
   - id (Primary Key)
   - attempt_id (Foreign Key → test_attempts.id)
   - question_id (Foreign Key → questions.id)
   - selected_option (A/B/C/D)

## Default Credentials

### Admin Account
- **Email:** admin@placement.com
- **Password:** admin123

**Note:** The admin account is automatically created on first startup.

## Getting Started

### Prerequisites
- Docker and Docker Compose installed
- For local development: Java 8+, Maven 3.6+, Node.js 18+, MySQL 8.0

### Running with Docker (Recommended)

1. **Clone the repository** (if applicable) or navigate to the project directory

2. **Start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api

4. **Stop the services:**
   ```bash
   docker-compose down
   ```

5. **Stop and remove volumes (clean database):**
   ```bash
   docker-compose down -v
   ```

### Running Locally (Without Docker)

#### Backend Setup

1. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

2. **Update database configuration** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/placement_test_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Backend will run on http://localhost:8080

#### Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Update API URL** in `src/services/api.js` if needed:
   ```javascript
   baseURL: 'http://localhost:8080/api'
   ```

4. **Start development server:**
   ```bash
   npm start
   ```

   Frontend will run on http://localhost:3000

## API Documentation

### Base URL
- Docker: `http://localhost:8080/api`
- Local: `http://localhost:8080/api`

### Authentication
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

### Endpoints

#### Authentication

**POST** `/api/auth/register`
- Register a new user
- Request Body:
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string"
  }
  ```
- Response: `AuthResponse` with token, email, username, role

**POST** `/api/auth/login`
- Login user
- Request Body:
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```
- Response: `AuthResponse` with token, email, username, role

#### Test (Student)

**GET** `/api/test/questions`
- Get 50 shuffled questions for the test
- Requires: Authentication (USER role)
- Response: Array of `QuestionDto` (without correct answers)

**POST** `/api/test/submit`
- Submit test answers
- Requires: Authentication (USER role)
- Request Body:
  ```json
  {
    "answers": {
      "questionId1": "A",
      "questionId2": "B",
      ...
    }
  }
  ```
- Response: `TestResultDto`

**GET** `/api/test/result`
- Get user's test result
- Requires: Authentication (USER role)
- Response: `TestResultDto`

#### Admin

**GET** `/api/admin/dashboard`
- Get dashboard statistics
- Requires: Authentication (ADMIN role)
- Response: `DashboardStatsDto`

**GET** `/api/admin/questions`
- Get all questions (with correct answers)
- Requires: Authentication (ADMIN role)
- Response: Array of `QuestionDto`

**GET** `/api/admin/questions/{id}`
- Get question by ID
- Requires: Authentication (ADMIN role)
- Response: `QuestionDto`

**POST** `/api/admin/questions`
- Create a new question
- Requires: Authentication (ADMIN role)
- Request Body:
  ```json
  {
    "questionText": "string",
    "optionA": "string",
    "optionB": "string",
    "optionC": "string",
    "optionD": "string",
    "correctOption": "A|B|C|D"
  }
  ```
- Response: `QuestionDto`

**PUT** `/api/admin/questions/{id}`
- Update a question
- Requires: Authentication (ADMIN role)
- Request Body: Same as POST
- Response: `QuestionDto`

**DELETE** `/api/admin/questions/{id}`
- Delete a question
- Requires: Authentication (ADMIN role)
- Response: Success message

**GET** `/api/admin/results`
- Get all student results
- Requires: Authentication (ADMIN role)
- Response: Array of `StudentResultDto`

**GET** `/api/admin/users`
- Get all users
- Requires: Authentication (ADMIN role)
- Response: Array of `User`

**POST** `/api/admin/users/{userId}/reset-password`
- Reset user password
- Requires: Authentication (ADMIN role)
- Response: `PasswordResetResponse` with temporary password

**PUT** `/api/admin/users/{userId}/status`
- Enable/Disable user
- Requires: Authentication (ADMIN role)
- Request Body:
  ```json
  {
    "enabled": true|false
  }
  ```
- Response: Success message

## Security Features

- **Password Hashing:** BCrypt with salt rounds
- **JWT Authentication:** Stateless token-based authentication
- **Role-Based Access Control:** ADMIN and USER roles
- **CORS Configuration:** Configured for frontend origin
- **SQL Injection Protection:** JPA parameterized queries
- **XSS Protection:** Input validation and sanitization

## Important Notes

1. **Test Attempt Limit:** Each user can attempt the test only once
2. **Question Shuffling:** Questions are shuffled per user session
3. **Password Security:** Passwords are never stored in plain text
4. **Admin Password Reset:** Generates a random 10-character temporary password
5. **Database:** MySQL database is automatically created on first run

## Troubleshooting

### Backend won't start
- Check if MySQL is running and accessible
- Verify database credentials in `application.properties`
- Check if port 8080 is available

### Frontend can't connect to backend
- Verify backend is running on port 8080
- Check CORS configuration
- Verify API URL in `src/services/api.js`

### Database connection issues
- Ensure MySQL container is healthy: `docker-compose ps`
- Check MySQL logs: `docker-compose logs mysql`
- Verify database credentials

### Docker build fails
- Ensure Docker has enough resources allocated
- Try rebuilding without cache: `docker-compose build --no-cache`
- Check Docker logs: `docker-compose logs`

## Development

### Adding Questions
1. Login as admin
2. Navigate to Admin Dashboard → Questions
3. Click "Add New Question"
4. Fill in question details and correct option
5. Click "Create"

### Testing the Application
1. Register a new student account
2. Login and take the test
3. View results
4. Login as admin to view all results and manage questions

## License

This project is created for educational purposes.

## Support

For issues or questions, please check the troubleshooting section or review the code comments.

