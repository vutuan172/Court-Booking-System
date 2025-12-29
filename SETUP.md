# 🚀 Hướng Dẫn Cài Đặt và Chạy Court Booking System

## 📋 Yêu Cầu Hệ Thống

- **Docker Desktop** (phiên bản mới nhất)
- **Docker Compose** (đi kèm với Docker Desktop)
- **Git** để clone repository
- **4GB RAM** tối thiểu
- **10GB** dung lượng ổ đĩa trống

## 📥 Bước 1: Clone Repository

```bash
git clone https://github.com/Quoc526/Court-Booking-System.git
cd Court-Booking-System
```

## 🐳 Bước 2: Cài Đặt Docker Desktop

### Trên macOS:
1. Tải Docker Desktop từ: https://www.docker.com/products/docker-desktop
2. Cài đặt và khởi động Docker Desktop
3. Đợi Docker Desktop hiển thị "Docker is running"

### Trên Windows:
1. Tải Docker Desktop từ: https://www.docker.com/products/docker-desktop
2. Cài đặt và enable WSL 2
3. Khởi động Docker Desktop
4. Đợi Docker Desktop running

### Trên Linux:
```bash
# Cài đặt Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Cài đặt Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

## ▶️ Bước 3: Chạy Ứng Dụng

### Khởi động lần đầu (build từ source):

```bash
docker compose up -d
```

Hoặc nếu muốn build lại từ đầu:

```bash
docker compose down
docker compose build --no-cache
docker compose up -d
```

### Kiểm tra trạng thái:

```bash
docker compose ps
```

Bạn sẽ thấy 2 containers đang chạy:
- `court-booking-app` (Spring Boot application)
- `court-booking-mysql` (MySQL database)

### Xem logs:

```bash
# Xem logs của app
docker compose logs -f app

# Xem logs của database
docker compose logs -f mysql
```

## 🌐 Bước 4: Truy Cập Ứng Dụng

Đợi khoảng **30-60 giây** để ứng dụng khởi động hoàn toàn, sau đó:

1. Mở trình duyệt web
2. Truy cập: **http://localhost:8080**

### Các tài khoản test có sẵn:

#### Admin Account:
- Email: `admin@example.com`
- Password: `password`

#### Court Owner Account:
- Email: `user@example.com`
- Password: `password`

#### Regular User Account:
- Email: `dinhquoctuan859@gmail.com`
- Password: `password`

## 🛠️ Các Lệnh Quản Lý

### Dừng ứng dụng:
```bash
docker compose down
```

### Khởi động lại:
```bash
docker compose restart
```

### Xóa tất cả (bao gồm database):
```bash
docker compose down -v
```

### Rebuild khi có thay đổi code:
```bash
docker compose down
docker compose build app
docker compose up -d
```

## 🔍 Kiểm Tra Health

Kiểm tra ứng dụng đã sẵn sàng chưa:

```bash
curl http://localhost:8080/actuator/health
```

Nếu thấy `{"status":"UP"}` là ứng dụng đã sẵn sàng!

## 📊 Truy Cập Database (Optional)

Nếu muốn xem database trực tiếp:

```bash
docker exec -it court-booking-mysql mysql -uroot -prootpassword court_booking
```

## ❌ Xử Lý Lỗi Thường Gặp

### Lỗi: Port 8080 đã được sử dụng
```bash
# Tìm process đang dùng port 8080
lsof -i :8080

# Kill process đó
kill -9 <PID>

# Hoặc thay đổi port trong docker-compose.yml
```

### Lỗi: Port 3306 (MySQL) đã được sử dụng
```bash
# Dừng MySQL local nếu có
brew services stop mysql  # macOS
sudo systemctl stop mysql # Linux
net stop MySQL            # Windows
```

### Lỗi: Docker daemon không chạy
```bash
# Khởi động Docker Desktop
# hoặc
sudo systemctl start docker  # Linux
```

### Lỗi: Container bị crash liên tục
```bash
# Xem logs để debug
docker compose logs app

# Xóa và rebuild
docker compose down -v
docker compose build --no-cache
docker compose up -d
```

## 🎯 Các Chức Năng Chính

1. **Trang chủ**: http://localhost:8080
2. **Đăng ký**: http://localhost:8080/register
3. **Đăng nhập**: http://localhost:8080/login
4. **Dashboard**: http://localhost:8080/dashboard (sau khi login)
5. **Booking**: http://localhost:8080/booking?courtId=1

## 📱 Chức Năng Theo Vai Trò

### User (Người dùng):
- Xem danh sách sân
- Đặt sân theo thời gian
- Xem lịch sử đặt sân
- Hủy booking
- Đánh giá sân

### Court Owner (Chủ sân):
- Quản lý sân của mình
- Thêm/sửa/xóa sân
- Xem bookings cho sân của mình
- Approve/Reject bookings
- Xem thống kê

### Admin:
- Quản lý tất cả users
- Quản lý tất cả courts
- Xem tất cả bookings
- Xem báo cáo hệ thống

## 🔧 Development

Nếu muốn phát triển thêm:

1. Sửa code trong thư mục `src/`
2. Rebuild container:
   ```bash
   docker compose down
   docker compose build app
   docker compose up -d
   ```

3. Hoặc dùng hot reload với Maven:
   ```bash
   mvn spring-boot:run
   ```

## 📞 Hỗ Trợ

Nếu gặp vấn đề, hãy:
1. Kiểm tra logs: `docker compose logs -f`
2. Restart containers: `docker compose restart`
3. Rebuild từ đầu: `docker compose down -v && docker compose up -d`

## ✅ Checklist Hoàn Thành

- [ ] Docker Desktop đã cài và đang chạy
- [ ] Clone repository thành công
- [ ] Chạy `docker compose up -d` không có lỗi
- [ ] Truy cập http://localhost:8080 thành công
- [ ] Login với tài khoản test được
- [ ] Đặt sân thành công

Chúc bạn sử dụng ứng dụng thành công! 🎉
