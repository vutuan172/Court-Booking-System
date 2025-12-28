# Court Booking Management System

A comprehensive web application for managing court bookings (football, badminton, tennis, etc.) built with Java Spring Boot.

## 🚀 Features

- **User Management**: Registration, login with role-based access (USER/ADMIN)
- **Court Management**: CRUD operations for courts with different types
- **Booking System**: 
  - Real-time availability checking
  - Concurrent booking prevention with database-level unique constraints
  - Transactional booking with optional service items
  - Cancellation with configurable time policy
- **Service Items**: Equipment rental and refreshments
- **Review System**: Users can rate and review completed bookings
- **Admin Dashboard**: Booking management, reports, and analytics
- **Reports**: Revenue reports and top services statistics

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Thymeleaf
- **MySQL**
- **Lombok**
- **Maven**

## 📋 Prerequisites

### Chạy với Docker (Khuyến nghị - Dễ nhất)
- Docker Desktop 20.10+
- Docker Compose V2
- 4GB RAM trống

### Chạy Local với Maven
- JDK 17 or higher
- Maven 3.8+
- MySQL 8.0+

## ⚙️ Quick Start

### 🐳 Option 1: Docker Compose (Khuyến nghị)

Cách nhanh nhất để chạy toàn bộ hệ thống:

```bash
# 1. Clone repository
git clone <repository-url>
cd demohethongdatsan

# 2. Build và chạy
docker compose up --build

# 3. Truy cập ứng dụng
open http://localhost:8080
```

**Xem hướng dẫn chi tiết tại: [DOCKER_SETUP.md](./DOCKER_SETUP.md)**

### 🔧 Option 2: Chạy Local với Maven

#### 1. Clone the Repository

```bash
git clone <repository-url>
cd demohethongdatsan
```

#### 2. Setup MySQL

**Dùng Docker (dễ nhất):**
```bash
docker run -d --name mysql-booking \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=yourpassword \
  -e MYSQL_DATABASE=court_booking \
  mysql:8.0
```

**Hoặc MySQL native:**
```sql
CREATE DATABASE court_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. Configure Database Connection

File `src/main/resources/application.yml` đã được config để tự động lấy từ environment variables. Bạn có thể:

**Option A: Set environment variables**
```bash
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=yourpassword
```

**Option B: Chỉnh sửa application.yml** (cho development)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/court_booking?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: yourpassword
```

#### 4. Build và Run

```bash
# Build project
mvn clean package -DskipTests

# Run application
mvn spring-boot:run
```

Application sẽ chạy tại `http://localhost:8080`

## 👤 Default Credentials

The application seeds initial data on first run:

### Admin Account
- **Email**: `admin@example.com`
- **Password**: `strongpassword`

### Sample User Account
- **Email**: `user@example.com`
- **Password**: `password123`

## 📚 API Documentation

### Authentication Endpoints

#### Register New User
```http
POST /auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "0123456789"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/x-www-form-urlencoded

username=user@example.com&password=password123
```

### Court Endpoints

#### Get All Courts
```http
GET /api/courts?type=football&location=North
```

#### Get Court Schedules
```http
GET /api/courts/{courtId}/schedules?date=2024-12-15
```

### Booking Endpoints

#### Create Booking
```http
POST /api/bookings
Authorization: Required
Content-Type: application/json

{
  "courtId": 1,
  "scheduleId": 10,
  "note": "Birthday party",
  "orderItems": [
    {
      "serviceItemId": 1,
      "quantity": 2
    }
  ]
}
```

#### Get User Bookings
```http
GET /api/users/{userId}/bookings
Authorization: Required
```

#### Cancel Booking
```http
POST /api/bookings/{id}/cancel
Authorization: Required
```

#### Create Review
```http
POST /api/bookings/{id}/reviews
Authorization: Required
Content-Type: application/json

{
  "rating": 5,
  "comment": "Excellent court and service!"
}
```

### Admin Endpoints

All admin endpoints require `ROLE_ADMIN` and start with `/api/admin/`

#### Manage Courts
```http
GET    /api/admin/courts
POST   /api/admin/courts
PUT    /api/admin/courts/{id}
DELETE /api/admin/courts/{id}
```

#### Manage Services
```http
GET    /api/admin/services
POST   /api/admin/services
PUT    /api/admin/services/{id}
DELETE /api/admin/services/{id}
```

#### Manage Bookings
```http
GET /api/admin/bookings?date=2024-12-15&status=CONFIRMED
PUT /api/admin/bookings/{id}/status
```

#### Reports
```http
GET /api/admin/reports/revenue?from=2024-12-01&to=2024-12-31
GET /api/admin/reports/top-services?from=2024-12-01&to=2024-12-31
```

### Public Endpoints

#### Get Service Items
```http
GET /api/public/services
```

#### Get Reviews
```http
GET /api/public/reviews?courtId=1
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Import Postman Collection

A Postman collection is provided in `Court_Booking_API.postman_collection.json`

1. Open Postman
2. Click Import
3. Select the JSON file
4. Collection includes all major API scenarios

## 🏗️ Project Structure

```
src/main/java/com/example/booking/
├── config/              # Configuration classes
│   ├── SecurityConfig.java
│   └── DataSeeder.java
├── controller/          # REST controllers
│   ├── AuthController.java
│   ├── BookingController.java
│   ├── CourtController.java
│   ├── AdminController.java
│   └── PublicController.java
├── dto/                 # Data Transfer Objects
├── entity/              # JPA entities
│   ├── enums/          # Enums
│   ├── BaseEntity.java
│   ├── User.java
│   ├── Court.java
│   ├── Schedule.java
│   ├── Booking.java
│   └── ...
├── exception/           # Custom exceptions
├── repository/          # JPA repositories
├── security/            # Security components
└── service/             # Business logic
    └── impl/           # Service implementations
```

## 🔐 Security Features

- **BCrypt Password Hashing**: All passwords are encrypted
- **Role-Based Access Control**: USER and ADMIN roles
- **Session-Based Authentication**: Secure session management
- **CSRF Protection**: Enabled for form submissions
- **Authorization Checks**: Endpoint-level security

## 📊 Database Schema

Key tables and relationships:

- `users` ↔ `user_roles` ↔ `roles` (Many-to-Many)
- `courts` → `schedules` (One-to-Many)
- `users` → `bookings` ← `schedules` (Many-to-One)
- `bookings` → `orders` → `order_details` ← `service_items`
- `bookings` → `reviews`
- `bookings` ← `bills` → `orders`

### Unique Constraints

The system prevents duplicate bookings using:
- Database-level unique constraint: `UNIQUE(court_id, date, start_time)` on `schedules`
- Pessimistic locking in booking transaction
- Schedule status management (AVAILABLE/BOOKED/BLOCKED)

## 🐳 Docker Support

### Using Docker Compose

```bash
docker-compose up -d
```

This starts:
- MySQL database on port 3306
- Spring Boot application on port 8080

### Environment Variables

Configure via environment variables:
- `DB_HOST`: MySQL host (default: localhost)
- `DB_PORT`: MySQL port (default: 3306)
- `DB_NAME`: Database name (default: court_booking)
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password

## ⚙️ Configuration

### Cancellation Policy

Modify `application.yml`:

```yaml
booking:
  cancellation:
    min-hours-before: 2  # Minimum hours before booking start time
```

### Data Seeding

Disable automatic data seeding:

```yaml
booking:
  seeding:
    enabled: false
```

## 🔄 Business Logic Highlights

### Booking Flow
1. User selects court and schedule
2. System checks availability with pessimistic lock
3. Optional service items added
4. Total price calculated (schedule price + service items)
5. Schedule marked as BOOKED
6. Transaction committed atomically

### Concurrency Handling
- **Pessimistic Locking**: `SELECT ... FOR UPDATE` on schedule
- **Unique Constraint**: Database prevents duplicate entries
- **Transaction Isolation**: All booking operations are transactional

### Review System
- Only users who made the booking can review
- Booking must be in DONE status
- One review per booking per user
- Auto-approved for simplicity (can be changed to PENDING)

## 📈 Sample Use Cases

### 1. User Books a Court
```
User → Browse courts → Select schedule → Add equipment → Confirm booking
```

### 2. Admin Manages System
```
Admin → View all bookings → Update status → Generate reports
```

### 3. User Cancels Booking
```
User → View bookings → Cancel (if within policy) → Schedule freed
```

## 🐛 Troubleshooting

### Database Connection Issues
- Check MySQL is running: `mysql -u root -p`
- Verify credentials in `application.yml`
- Ensure database exists

### Port Already in Use
Change port in `application.yml`:
```yaml
server:
  port: 8081
```

### Build Errors
```bash
mvn clean install -U
```

## 📝 License

This project is for educational/demonstration purposes.

## 👥 Contact

For questions or support, please contact the development team.

---

**Happy Booking! 🎾⚽🏸**
