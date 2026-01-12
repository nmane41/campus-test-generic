# Young Element Placement Test System

A complete, production-ready web application for conducting online campus placement tests with role-based access control (Admin and Student).

## Features

### Student Features
- User registration with username, email, and password
- User login using email + password
- **Welcome screen** - Users wait for admin to start the test (no manual test start)
- **Auto-start test** - Automatically redirected to test when admin starts it (polling every 5 seconds)
- Take a placement test (50 MCQ questions)
- **50-minute timer** - Visible countdown with auto-submit when time expires
- Questions are shuffled per user
- **Non-mandatory questions** - Users can skip questions
- View test results after submission
- Automatic score calculation
- **Time taken tracking** - Displays test duration in mm:ss format

### Admin Features
- Admin dashboard with statistics:
  - Total registered users
  - Currently active users
  - Total test attempts
  - Average score
- **Start Test** - Central control to start test for all logged-in users
- Question Management (CRUD operations)
- View all students' results with:
  - Student name, email, score
  - Time taken (mm:ss format)
  - Start time and end time (IST, 12-hour AM/PM format)
- **Download Results as PDF** - Export all results with IST formatted times
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
- OpenPDF (for PDF generation)

### Frontend
- React 18.2
- React Router 6.8
- Axios
- CSS3

### Deployment
- Docker
- Docker Compose
- Nginx (for frontend)
- AWS EC2 (production deployment)

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
│   │       │   └── util/            # JWT Utility & Time Formatter
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

5. **test_status** (NEW)
   - id (Primary Key)
   - status (STARTED/NOT_STARTED)
   - test_start_time
   - updated_at

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

## Deployment on AWS EC2

### Prerequisites
- AWS Account
- EC2 Instance (Ubuntu 20.04 LTS or later recommended)
- Minimum requirements:
  - 2 GB RAM
  - 2 vCPU
  - 20 GB storage
- Domain name (optional, for custom domain)

### Step 1: Launch EC2 Instance

1. **Go to AWS Console → EC2 → Launch Instance**
2. **Choose Ubuntu Server 20.04 LTS or later**
3. **Select instance type:** t2.medium or larger (t2.micro may work but not recommended)
4. **Configure Security Group:**
   - SSH (22) - Your IP
   - HTTP (80) - 0.0.0.0/0
   - HTTPS (443) - 0.0.0.0/0 (if using SSL)
   - Custom TCP (8080) - 0.0.0.0/0 (for backend API, optional)
5. **Create/Select Key Pair** and download `.pem` file
6. **Launch Instance**

### Step 2: Connect to EC2 Instance

```bash
# Change permissions for key file
chmod 400 your-key.pem

# Connect to instance
ssh -i your-key.pem ubuntu@your-ec2-public-ip
```

### Step 3: Install Docker and Docker Compose

```bash
# Update system packages
sudo apt-get update

# Install Docker
sudo apt-get install -y docker.io

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add current user to docker group (to run docker without sudo)
sudo usermod -aG docker ubuntu

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installations
docker --version
docker-compose --version

# Logout and login again for group changes to take effect
exit
# Then reconnect via SSH
```

### Step 4: Clone/Upload Project to EC2

**Option A: Using Git (Recommended)**
```bash
# Install Git
sudo apt-get install -y git

# Clone your repository
git clone <your-repository-url>
cd campus-test-generic
```

**Option B: Using SCP (if not using Git)**
```bash
# From your local machine
scp -i your-key.pem -r /path/to/project ubuntu@your-ec2-public-ip:~/
```

### Step 5: Configure Environment Variables

Update `docker-compose.yml` if needed, or create a `.env` file:

```bash
# Create .env file (optional)
nano .env
```

Add any custom environment variables if needed.

### Step 6: Update Application Configuration

**Update `docker-compose.yml` for production:**

```yaml
# Ensure MySQL password is strong
environment:
  MYSQL_ROOT_PASSWORD: your_strong_password_here

# Update backend environment variables if needed
backend:
  environment:
    SPRING_DATASOURCE_PASSWORD: your_strong_password_here
```

**Update frontend API configuration** (if using custom domain):

Edit `frontend/src/services/api.js`:
```javascript
const api = axios.create({
  baseURL: '/api',  // This works with nginx proxy
  // Or use your domain: baseURL: 'https://yourdomain.com/api',
});
```

### Step 7: Build and Start Services

```bash
# Navigate to project directory
cd ~/campus-test-generic

# Build and start all services
docker-compose up -d --build

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### Step 8: Configure Firewall (UFW)

```bash
# Allow HTTP and HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp
sudo ufw enable

# Check status
sudo ufw status
```

### Step 9: Access Your Application

- **Frontend:** http://your-ec2-public-ip
- **Backend API:** http://your-ec2-public-ip/api

### Step 10: Set Up Domain Name (Optional)

1. **Point your domain to EC2 IP:**
   - Go to your domain registrar
   - Add A record: `@` → `your-ec2-public-ip`
   - Add A record: `www` → `your-ec2-public-ip`

2. **Update Nginx configuration** (if using custom domain):

   Create `frontend/nginx-custom.conf`:
   ```nginx
   server {
     listen 80;
     server_name yourdomain.com www.yourdomain.com;

     location / {
       root /usr/share/nginx/html;
       index index.html;
       try_files $uri /index.html;
     }

     location /api {
       proxy_pass http://backend:8080;
       proxy_set_header Host $host;
       proxy_set_header X-Real-IP $remote_addr;
       proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
     }
   }
   ```

   Update `frontend/Dockerfile` to use custom config:
   ```dockerfile
   COPY nginx-custom.conf /etc/nginx/conf.d/default.conf
   ```

   Rebuild frontend:
   ```bash
   docker-compose up -d --build frontend
   ```

### Step 11: Set Up SSL/HTTPS (Recommended)

**Using Let's Encrypt with Certbot:**

```bash
# Install Certbot
sudo apt-get install -y certbot python3-certbot-nginx

# Stop nginx in docker temporarily
docker-compose stop frontend

# Install nginx on host (temporary, for certbot)
sudo apt-get install -y nginx

# Get SSL certificate
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# Certificates will be saved to:
# /etc/letsencrypt/live/yourdomain.com/fullchain.pem
# /etc/letsencrypt/live/yourdomain.com/privkey.pem

# Update nginx configuration to use SSL
# (You'll need to mount certificates into docker container)
```

**Alternative: Use AWS Certificate Manager (ACM) with Application Load Balancer**

### Step 12: Set Up Auto-Start on Reboot

Create a systemd service:

```bash
# Create service file
sudo nano /etc/systemd/system/placement-test.service
```

Add:
```ini
[Unit]
Description=Placement Test System
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/ubuntu/campus-test-generic
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
User=ubuntu
Group=ubuntu

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable placement-test.service
sudo systemctl start placement-test.service
```

### Maintenance Commands

```bash
# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql

# Restart services
docker-compose restart

# Stop services
docker-compose stop

# Start services
docker-compose start

# Rebuild after code changes
docker-compose up -d --build

# Backup database
docker-compose exec mysql mysqldump -u root -p placement_test_db > backup.sql

# Restore database
docker-compose exec -T mysql mysql -u root -p placement_test_db < backup.sql
```

### Monitoring and Troubleshooting

**Check service health:**
```bash
docker-compose ps
docker stats
```

**Check disk space:**
```bash
df -h
docker system df
```

**Clean up unused Docker resources:**
```bash
docker system prune -a
```

**View application logs:**
```bash
# Backend logs
docker-compose logs backend | tail -100

# Frontend logs
docker-compose logs frontend | tail -100

# MySQL logs
docker-compose logs mysql | tail -100
```

## API Documentation

### Base URL
- Local: `http://localhost:8080/api`
- Production: `http://your-domain.com/api` or `http://your-ec2-ip/api`

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

**GET** `/api/test/status`
- Get test status (STARTED/NOT_STARTED)
- Requires: Authentication
- Response: Test status and start time

**GET** `/api/test/questions`
- Get 50 shuffled questions for the test
- Requires: Authentication (USER role), Test must be STARTED
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
- Note: Questions are not mandatory; unanswered questions are treated as incorrect

**GET** `/api/test/result`
- Get user's test result
- Requires: Authentication (USER role)
- Response: `TestResultDto`

#### Admin

**GET** `/api/admin/dashboard`
- Get dashboard statistics
- Requires: Authentication (ADMIN role)
- Response: `DashboardStatsDto`

**POST** `/api/admin/start-test`
- Start test for all users
- Requires: Authentication (ADMIN role)
- Response: Success message

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
- Get all student results with IST formatted times
- Requires: Authentication (ADMIN role)
- Response: Array of `StudentResultDto` with:
  - startTimeIST (IST, 12-hour format)
  - endTimeIST (IST, 12-hour format)
  - timeTakenFormatted (mm:ss)

**GET** `/api/admin/results/export/pdf`
- Download results as PDF
- Requires: Authentication (ADMIN role)
- Response: PDF file with all student results

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
- **Test Access Control:** Users cannot access test before admin starts it
- **Timer Validation:** Backend validates 50-minute time limit

## Important Notes

1. **Test Attempt Limit:** Each user can attempt the test only once
2. **Question Shuffling:** Questions are shuffled per user session
3. **Password Security:** Passwords are never stored in plain text
4. **Admin Password Reset:** Generates a random 10-character temporary password
5. **Database:** MySQL database is automatically created on first run
6. **Test Start Control:** Only admin can start the test; users wait on welcome screen
7. **Auto-Start:** Users are automatically redirected when admin starts test (5-second polling)
8. **Time Format:** All times displayed in IST (Indian Standard Time) with 12-hour AM/PM format
9. **Timer:** 50-minute countdown timer with auto-submit when time expires
10. **Non-Mandatory Questions:** Users can skip questions; unanswered = incorrect

## Troubleshooting

### Backend won't start
- Check if MySQL is running and accessible
- Verify database credentials in `application.properties` or `docker-compose.yml`
- Check if port 8080 is available
- View backend logs: `docker-compose logs backend`

### Frontend can't connect to backend
- Verify backend is running on port 8080
- Check CORS configuration
- Verify API URL in `src/services/api.js`
- Check nginx proxy configuration
- View frontend logs: `docker-compose logs frontend`

### Database connection issues
- Ensure MySQL container is healthy: `docker-compose ps`
- Check MySQL logs: `docker-compose logs mysql`
- Verify database credentials
- Check if MySQL is ready: `docker-compose exec mysql mysqladmin ping -h localhost`

### Docker build fails
- Ensure Docker has enough resources allocated
- Try rebuilding without cache: `docker-compose build --no-cache`
- Check Docker logs: `docker-compose logs`
- Verify Docker and Docker Compose versions

### EC2 Deployment Issues
- Check security group rules (ports 80, 443, 22)
- Verify instance has enough resources (CPU, RAM, disk)
- Check EC2 instance status in AWS Console
- Review CloudWatch logs if configured
- Ensure Docker service is running: `sudo systemctl status docker`

### Test not starting
- Verify admin clicked "Start Test" button
- Check test_status table in database
- Verify backend logs for any errors
- Check if test status API is accessible: `curl http://your-domain/api/test/status`

### Timer issues
- Verify test start time is correctly set
- Check browser console for JavaScript errors
- Ensure backend timer validation is working
- Verify timezone settings (should be UTC in database, IST in display)

## Development

### Adding Questions
1. Login as admin
2. Navigate to Admin Dashboard → Questions
3. Click "Add New Question"
4. Fill in question details and correct option
5. Click "Create"

### Testing the Application
1. Register a new student account
2. Login - user will see welcome screen
3. Login as admin and click "Start Test"
4. Student will be automatically redirected to test
5. Complete test (or wait for auto-submit after 50 minutes)
6. View results
7. Admin can view all results and download PDF

## License

This project is created for educational purposes.

## Support

For issues or questions, please check the troubleshooting section or review the code comments.
