# 🐳 Hướng Dẫn Chạy Court Booking System với Docker

## 📋 Mục Lục
- [A) Docker Compose (Khuyến nghị)](#a-docker-compose-khuyến-nghị)
- [B) Build Docker Manual cho Mac M1/M2](#b-build-docker-manual-cho-mac-m1m2)
- [C) Chạy Local với Maven (Không Docker)](#c-chạy-local-với-maven-không-docker)

---

## A) Docker Compose (Khuyến nghị)

### ✅ Yêu cầu
- Docker Desktop 20.10+ 
- Docker Compose V2
- 4GB RAM trống

### 🚀 Các Bước Chạy

#### 1. Clone và di chuyển vào thư mục project
```bash
cd /Users/gtuan/demohethongdatsan
```

#### 2. Dọn dẹp container cũ (nếu có)
```bash
docker compose down -v
docker system prune -f
```

#### 3. Build và chạy toàn bộ hệ thống
```bash
# Build và start services
docker compose up --build -d

# Hoặc chạy foreground để xem logs trực tiếp
docker compose up --build
```

#### 4. Kiểm tra trạng thái
```bash
# Xem logs của app
docker compose logs -f app

# Xem logs của MySQL
docker compose logs -f mysql

# Kiểm tra health
docker compose ps
```

#### 5. Test ứng dụng
```bash
# Kiểm tra health endpoint
curl http://localhost:8080/actuator/health

# Truy cập web UI
open http://localhost:8080

# Hoặc dùng browser: http://localhost:8080
```

### 🔍 Debug Logs

```bash
# Xem logs theo thời gian thực
docker compose logs -f

# Xem 100 dòng logs cuối
docker compose logs --tail=100 app

# Xem logs từ 10 phút trước
docker compose logs --since 10m app

# Vào shell của container
docker compose exec app sh
docker compose exec mysql bash
```

### 🛑 Dừng và xóa

```bash
# Dừng services
docker compose stop

# Dừng và xóa containers
docker compose down

# Dừng và xóa tất cả (bao gồm volumes - MẤT DATA!)
docker compose down -v
```

### ⚠️ Xử lý lỗi thường gặp

**Lỗi: "port 3306 already in use"**
```bash
# Tìm process đang dùng port 3306
lsof -i :3306
# Kill process hoặc đổi port trong docker-compose.yml
```

**Lỗi: "platform mismatch" trên Mac M1/M2**
```bash
# Uncomment dòng này trong docker-compose.yml:
# platform: linux/amd64
# Sau đó chạy lại: docker compose up --build
```

**Lỗi: MySQL chưa ready**
```bash
# Đợi thêm 30s, healthcheck sẽ tự retry
# Hoặc kiểm tra: docker compose logs mysql
```

---

## B) Build Docker Manual cho Mac M1/M2

### Khi nào dùng?
- Multi-arch build tự động không hoạt động
- Cần build image riêng cho platform cụ thể
- Deploy lên server khác architecture

### 🔧 Build cho AMD64 (Intel)

```bash
# 1. Tạo builder mới (chỉ cần làm 1 lần)
docker buildx create --name multiarch-builder --use
docker buildx inspect --bootstrap

# 2. Build image cho linux/amd64
docker buildx build \
  --platform linux/amd64 \
  -t booking:amd64-latest \
  --load \
  .

# 3. Chạy container
docker run -d \
  --name booking-app \
  --platform linux/amd64 \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/court_booking?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  booking:amd64-latest
```

### 🔧 Build cho ARM64 (Apple Silicon)

```bash
# Build native cho ARM64
docker buildx build \
  --platform linux/arm64 \
  -t booking:arm64-latest \
  --load \
  .

# Chạy container
docker run -d \
  --name booking-app \
  --platform linux/arm64 \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/court_booking?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  booking:arm64-latest
```

### 🌐 Build Multi-Arch (Push to Registry)

```bash
# Build và push cả 2 platforms (cần Docker Hub account)
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t yourusername/booking:latest \
  --push \
  .
```

---

## C) Chạy Local với Maven (Không Docker)

### ✅ Yêu cầu
- JDK 17+
- Maven 3.8+
- MySQL 8.0+ (chạy local hoặc Docker)

### 📦 Cài đặt Dependencies

#### Cài Maven (nếu chưa có)

**macOS:**
```bash
# Dùng Homebrew
brew install maven

# Verify
mvn -v
```

**Linux:**
```bash
sudo apt-get update
sudo apt-get install maven
```

**Windows:**
```powershell
# Dùng Chocolatey
choco install maven

# Hoặc download từ: https://maven.apache.org/download.cgi
```

### 🗄️ Setup MySQL Local

#### Option 1: MySQL trong Docker
```bash
docker run -d \
  --name mysql-booking \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=yourpassword \
  -e MYSQL_DATABASE=court_booking \
  mysql:8.0

# Import schema (optional)
docker exec -i mysql-booking mysql -uroot -pyourpassword court_booking < schema.sql
```

#### Option 2: MySQL Native
```bash
# Cài MySQL
brew install mysql  # macOS
# hoặc sudo apt-get install mysql-server  # Linux

# Start MySQL
brew services start mysql  # macOS
# hoặc sudo systemctl start mysql  # Linux

# Tạo database
mysql -u root -p
CREATE DATABASE court_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

### ⚙️ Cấu hình application.yml

File `src/main/resources/application.yml` đã được config để dùng biến môi trường, nhưng bạn có thể override:

**Cách 1: Dùng biến môi trường**
```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/court_booking?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=yourpassword
```

**Cách 2: Tạo file `application-local.yml`**
```yaml
# src/main/resources/application-local.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/court_booking?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: yourpassword
```

### 🚀 Build và Run

```bash
# 1. Clean và build
mvn clean package -DskipTests

# 2. Run ứng dụng
mvn spring-boot:run

# Hoặc chạy jar file trực tiếp
java -jar target/booking-1.0.0.jar

# Hoặc với profile local
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 🔍 Verify

```bash
# Check health
curl http://localhost:8080/actuator/health

# Mở browser
open http://localhost:8080
```

### 🐛 Debug Mode

```bash
# Run với debug port 5005
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Hoặc dùng IDE (IntelliJ/Eclipse) để run và debug trực tiếp
```

---

## 🧪 Test với Postman

### Import Collection

1. Mở Postman
2. Click **Import** → **File**
3. Chọn `Court_Booking_API.postman_collection.json`
4. Collection sẽ xuất hiện với biến `{{baseUrl}}` = `http://localhost:8080`

### Test Scenarios

#### Scenario 1: User Registration & Login
```
1. POST /auth/register - Tạo user mới
2. POST /auth/login - Đăng nhập (lưu session cookie)
3. GET /api/users/{userId}/bookings - Xem bookings của user
```

#### Scenario 2: Browse và Book Court
```
1. GET /api/courts - Xem tất cả courts
2. GET /api/courts/{courtId}/schedules?date=2024-12-15 - Xem lịch available
3. POST /api/bookings - Tạo booking mới (+ optional service items)
4. GET /api/bookings/{bookingId} - Xem chi tiết booking
```

#### Scenario 3: Cancel và Review
```
1. POST /api/bookings/{bookingId}/cancel - Hủy booking
2. POST /api/bookings/{bookingId}/reviews - Để lại review (booking phải DONE)
```

#### Scenario 4: Admin Operations (Login as admin first)
```
1. POST /auth/login (admin@example.com / strongpassword)
2. POST /api/admin/courts - Tạo court mới
3. GET /api/admin/bookings - Xem tất cả bookings
4. PUT /api/admin/bookings/{bookingId}/status - Đổi status
5. GET /api/admin/reports/revenue?from=2024-12-01&to=2024-12-31
```

#### Test Duplicate Booking (Concurrency Test)
```
1. Tạo booking cho schedule ID = 1
2. Ngay lập tức tạo booking khác cho cùng schedule ID = 1
3. Request thứ 2 phải FAIL với error "409 Conflict"
```

---

## 📊 Log Patterns để Monitor

### ✅ Success Patterns
```
✓ Started BookingApplication in X.XXX seconds
✓ Tomcat started on port(s): 8080
✓ Created admin user: admin@example.com
✓ Created X courts
✓ Created X schedules
✓ Data seeding completed successfully
```

### ❌ Error Patterns cần fix
```
✗ Communications link failure - MySQL chưa ready
✗ Access denied for user - Sai password
✗ Unknown database 'court_booking' - Database chưa tạo
✗ no match for platform - Image không hỗ trợ architecture
```

---

## 🎯 Acceptance Checklist

- [ ] `docker compose up --build` chạy thành công
- [ ] `docker compose ps` - Tất cả services đều "healthy"
- [ ] `curl http://localhost:8080/actuator/health` trả về `{"status":"UP"}`
- [ ] Truy cập `http://localhost:8080` thấy trang home
- [ ] Postman flow: register → login → get courts → create booking → SUCCESS
- [ ] Test duplicate booking → FAIL với 409 Conflict
- [ ] Admin login → CRUD courts → SUCCESS
- [ ] Logs không có lỗi critical

---

## 🆘 Troubleshooting Common Issues

### Issue: Maven không tìm thấy
```bash
# Check maven
which mvn
mvn -v

# Nếu không có, cài đặt:
brew install maven  # macOS
```

### Issue: Port 8080 đã được sử dụng
```bash
# Tìm process
lsof -i :8080

# Kill process
kill -9 <PID>

# Hoặc đổi port trong docker-compose.yml:
# ports: - "8081:8080"
```

### Issue: Docker build quá chậm
```bash
# Clear cache và rebuild
docker builder prune -f
docker compose build --no-cache
```

### Issue: MySQL connection timeout
```bash
# Tăng timeout trong docker-compose.yml:
# healthcheck retries: 20
# start_period: 60s
```

---

## 📞 Support

Nếu gặp vấn đề không có trong guide này:
1. Check logs: `docker compose logs -f`
2. Kiểm tra health: `docker compose ps`
3. Xem README.md và QUICKSTART.md
4. Debug từng service riêng lẻ

**Happy Coding! 🚀**
