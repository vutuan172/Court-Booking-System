# 🧪 TEST HỆ THỐNG ĐẶT SÂN

## ✅ Hệ thống đã sẵn sàng 100%!

### 🚀 CÁCH TEST NHANH

#### 1. Kiểm tra hệ thống đang chạy:
```bash
curl http://localhost:8080/actuator/health
# Kết quả mong đợi: {"status":"UP"}
```

#### 2. Mở browser và test:
```
http://localhost:8080
```

---

## 📋 TEST SCENARIOS

### Scenario 1: USER THƯỜNG ĐẶT SÂN ⚽

**Bước 1: Đăng nhập**
- Vào http://localhost:8080
- Click nút "Login" ở góc phải
- Nhập:
  - Email: `user@example.com`
  - Password: `password123`
- Submit

**Bước 2: Xem Dashboard**
- Tự động redirect đến `/dashboard`
- Thấy 3 tabs: Available Courts, My Bookings, Admin Panel (bị ẩn vì không phải admin)
- Tab "Available Courts" đang active

**Bước 3: Xem danh sách sân**
- Thấy 4 sân hiển thị dạng grid:
  1. Football Field A - 150,000 VNĐ/giờ
  2. Badminton Court 1 - 80,000 VNĐ/giờ
  3. Tennis Court Blue - 120,000 VNĐ/giờ
  4. Futsal Arena - 100,000 VNĐ/giờ
- Mỗi card có: tên, loại, giá, địa điểm, mô tả, nút "Book Now"

**Bước 4: Đặt sân**
- Click nút "Book Now" trên Football Field A
- Redirect đến `/booking?courtId=1`
- Trang booking hiển thị:
  - Thông tin sân đã chọn
  - Date picker (không chọn được ngày quá khứ)
  - Chọn ngày: ví dụ `15/12/2025`

**Bước 5: Chọn giờ**
- Sau khi chọn ngày, system tự động load time slots
- Thấy 12 slots từ 9:00 đến 21:00
- Các slot available màu xanh lá
- Click chọn slot: ví dụ `14:00 - 15:00`

**Bước 6: Xác nhận**
- Thấy tổng tiền: `150,000 VNĐ`
- Click nút "Confirm Booking"
- Thấy success message
- Tự động redirect về Dashboard

**Bước 7: Xem lịch sử**
- Click tab "My Bookings"
- Thấy booking vừa tạo trong bảng
- Status: 🟡 PENDING
- Có đầy đủ thông tin: Booking ID, Court, Date/Time, Price

---

### Scenario 2: ADMIN QUẢN LÝ 👨‍💼

**Bước 1: Logout user thường**
- Click nút "Logout" ở header
- Redirect về trang chủ

**Bước 2: Login admin**
- Click "Login"
- Nhập:
  - Email: `admin@example.com`
  - Password: `strongpassword`
- Submit

**Bước 3: Xem Dashboard Admin**
- Redirect đến `/dashboard`
- Thấy badge "ADMIN" màu vàng cạnh tên
- Thấy đầy đủ 3 tabs (bao gồm Admin Panel)

**Bước 4: Xem thống kê**
- Click tab "Admin Panel"
- Thấy 4 thẻ thống kê:
  - 🏟️ Total Courts: 4
  - 📋 Total Bookings: (số lượng bookings hiện có)
  - 👥 Total Users: 2
  - 💰 Total Revenue: (tổng doanh thu)

**Bước 5: Quản lý bookings**
- Scroll xuống thấy bảng "All Bookings"
- Hiển thị TẤT CẢ bookings của tất cả users
- Có filter dropdown để lọc theo status
- Thông tin chi tiết: ID, User Email, Court Name, Date/Time, Price, Status

**Bước 6: Test filter**
- Click dropdown "Filter by Status"
- Chọn "PENDING"
- Bảng chỉ hiển thị các bookings đang PENDING

---

### Scenario 3: TEST API TRỰC TIẾP 🔌

#### Test 1: Lấy danh sách sân
```bash
curl http://localhost:8080/api/courts | jq
```
Kết quả: JSON với 4 courts

#### Test 2: Lấy schedules cho ngày cụ thể
```bash
curl "http://localhost:8080/api/courts/1/schedules?date=2025-12-15" | jq '.data | length'
```
Kết quả: 12 (12 time slots)

#### Test 3: Xem chi tiết 1 sân
```bash
curl http://localhost:8080/api/courts/1 | jq '.data'
```
Kết quả: Chi tiết Football Field A

#### Test 4: Health check
```bash
curl http://localhost:8080/actuator/health
```
Kết quả: `{"status":"UP"}`

---

## 🎯 CHECKLIST CHỨC NĂNG

### ✅ Authentication & Authorization
- [x] Login với user@example.com
- [x] Login với admin@example.com
- [x] Logout redirect về trang chủ
- [x] User không thấy Admin Panel tab
- [x] Admin thấy badge "ADMIN"

### ✅ Dashboard
- [x] Auto redirect khi đã login
- [x] Tab "Available Courts" hiển thị 4 sân
- [x] Filter sân theo type (All/Football/Badminton/Tennis/Futsal)
- [x] Nút "Book Now" hoạt động
- [x] Tab "My Bookings" hiển thị bookings của user
- [x] Filter bookings theo status
- [x] Tab "Admin Panel" chỉ admin thấy

### ✅ Booking Flow
- [x] Chọn sân → redirect đến booking page
- [x] Hiển thị thông tin sân đúng
- [x] Date picker không cho chọn quá khứ
- [x] Chọn ngày → load time slots tự động
- [x] 12 slots từ 9AM-9PM hiển thị
- [x] Click slot → highlight màu xanh đậm
- [x] Tính tổng tiền tự động
- [x] Confirm → tạo booking thành công

### ✅ Admin Features
- [x] Thống kê hiển thị đúng số liệu
- [x] Xem tất cả bookings của mọi user
- [x] Filter all bookings theo status
- [x] Doanh thu tính từ bookings DONE

### ✅ Data & Entities
- [x] 10 entities: User, Role, Court, Booking, Schedule, Review, Bill, Order, OrderDetail, ServiceItem
- [x] 2 roles: ROLE_USER, ROLE_ADMIN
- [x] 2 users mẫu
- [x] 4 courts mẫu
- [x] Schedules tự động generate khi cần

### ✅ APIs
- [x] GET /api/courts - danh sách sân
- [x] GET /api/courts/{id} - chi tiết sân
- [x] GET /api/courts/{id}/schedules?date=X - lịch theo ngày
- [x] POST /api/bookings - tạo booking (cần auth)
- [x] GET /api/bookings/my-bookings - lịch sử của mình (cần auth)
- [x] GET /api/admin/stats - thống kê (admin only)
- [x] GET /api/admin/bookings - tất cả bookings (admin only)

---

## 🐛 TROUBLESHOOTING

### Lỗi: Cannot connect to localhost:8080
```bash
# Check containers
docker compose ps

# Restart nếu cần
docker compose restart app

# Check logs
docker compose logs app --tail 50
```

### Lỗi: Login không được
```bash
# Verify users đã được seed
docker compose exec mysql mysql -u booking_user -pbooking_password booking_db -e "SELECT email FROM users;"

# Kết quả phải có: admin@example.com và user@example.com
```

### Lỗi: Dashboard trống
```bash
# Check API courts
curl http://localhost:8080/api/courts

# Phải trả về 4 courts
```

### Lỗi: Time slots không load
```bash
# Test schedules API
curl "http://localhost:8080/api/courts/1/schedules?date=2025-12-15"

# Phải trả về 12 slots
```

---

## 📊 DATABASE INFO

### Connect to MySQL
```bash
docker compose exec mysql mysql -u booking_user -pbooking_password booking_db
```

### Check tables
```sql
SHOW TABLES;
-- Kết quả: users, roles, user_roles, courts, bookings, schedules, reviews, bills, orders, order_details, service_items
```

### Check data
```sql
SELECT * FROM courts;
SELECT * FROM users;
SELECT * FROM bookings;
SELECT * FROM schedules LIMIT 10;
```

---

## 🎉 KẾT LUẬN

**HỆ THỐNG ĐÃ HOÀN THIỆN 100%!**

Tất cả chức năng hoạt động:
✅ Authentication (Login/Logout/Register)
✅ Authorization (User/Admin roles)
✅ Dashboard với 3 tabs
✅ Booking flow hoàn chỉnh
✅ Auto-generate schedules
✅ Admin statistics & management
✅ REST APIs đầy đủ
✅ 10 entities với relationships
✅ Docker multi-arch support
✅ Responsive UI

**Truy cập:** http://localhost:8080
**Admin:** admin@example.com / strongpassword
**User:** user@example.com / password123
