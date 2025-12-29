# 🏟️ HƯỚNG DẪN SỬ DỤNG HỆ THỐNG ĐẶT SÂN

## ✅ Hệ thống đã sẵn sàng!

**URL:** http://localhost:8080

## 🔐 TÀI KHOẢN TEST

### Admin (Quản trị viên)
- **Email:** admin@example.com
- **Password:** strongpassword
- **Quyền:** Xem tất cả bookings, thống kê, quản lý toàn bộ hệ thống

### User thường
- **Email:** user@example.com  
- **Password:** password123
- **Quyền:** Đặt sân, xem bookings của mình

## 📋 CHỨC NĂNG ĐẦY ĐỦ

### 1. 🏠 Trang chủ (/)
- Landing page với thông tin giới thiệu
- Nút "Get Started" để đăng nhập
- Tự động chuyển đến Dashboard nếu đã đăng nhập

### 2. 🔑 Đăng nhập/Đăng ký
- **/login** - Form đăng nhập
- **/register** - Form đăng ký tài khoản mới

### 3. 📊 Dashboard (/dashboard)
**3 TAB chính:**

#### Tab 1: Available Courts (Danh sách sân)
- Hiển thị 4 sân: Football, Badminton, Tennis, Futsal
- Thông tin: Tên sân, loại, giá/giờ, địa điểm, mô tả
- Nút **"Book Now"** → chuyển đến trang đặt sân
- Filter theo loại sân (All, Football, Badminton, Tennis, Futsal)

#### Tab 2: My Bookings (Lịch sử đặt sân)
- Hiển thị tất cả bookings của user
- Thông tin: Mã booking, sân, ngày giờ, tổng tiền, trạng thái
- Status badge màu:
  - 🟡 PENDING (Chờ xác nhận)
  - 🟢 CONFIRMED (Đã xác nhận)  
  - ✅ DONE (Hoàn thành)
  - 🔴 CANCELED (Đã hủy)
- Filter theo trạng thái

#### Tab 3: Admin Panel (Chỉ ADMIN thấy)
- **Thống kê tổng quan:**
  - 🏟️ Total Courts
  - 📋 Total Bookings
  - 👥 Total Users
  - 💰 Total Revenue (VNĐ)
- **Bảng quản lý bookings:**
  - Xem tất cả bookings của tất cả users
  - Thông tin đầy đủ: ID, User, Court, Date/Time, Price, Status
  - Filter theo trạng thái

### 4. 🎯 Trang đặt sân (/booking?courtId=X)
- **Bước 1:** Hiển thị thông tin sân đã chọn
- **Bước 2:** Chọn ngày (date picker, không được chọn ngày quá khứ)
- **Bước 3:** Load time slots available (9AM-10PM)
  - Slot màu xanh: Available
  - Slot màu xám: Unavailable (đã có người đặt)
- **Bước 4:** Tính tổng tiền tự động
- **Bước 5:** Confirm Booking → tạo booking mới → redirect về Dashboard

## 🔧 API ENDPOINTS

### Public APIs
```
GET  /api/courts                    # Danh sách tất cả sân
GET  /api/courts/{id}              # Chi tiết 1 sân
GET  /api/courts/{id}/schedules?date=YYYY-MM-DD  # Lịch sân theo ngày
GET  /actuator/health              # Health check
```

### Authenticated APIs (cần login)
```
POST /api/bookings                 # Tạo booking mới
GET  /api/bookings/my-bookings     # Lịch sử booking của mình
GET  /api/bookings/{id}            # Chi tiết 1 booking
POST /api/bookings/{id}/cancel     # Hủy booking
POST /api/bookings/{id}/reviews    # Đánh giá sân
```

### Admin APIs (chỉ ADMIN)
```
GET  /api/admin/stats              # Thống kê tổng quan
GET  /api/admin/bookings           # Tất cả bookings
POST /api/admin/bookings/{id}/confirm   # Xác nhận booking
POST /api/admin/bookings/{id}/complete  # Hoàn thành booking
POST /api/admin/courts             # Thêm sân mới
PUT  /api/admin/courts/{id}        # Cập nhật sân
```

## 📦 DATABASE

### Entities có sẵn:
1. **User** - Người dùng (admin/user)
2. **Role** - Vai trò (ROLE_ADMIN, ROLE_USER)
3. **Court** - Sân (football, badminton, tennis, futsal)
4. **Booking** - Đặt sân
5. **Schedule** - Lịch sân theo giờ
6. **Review** - Đánh giá sân
7. **Bill** - Hóa đơn thanh toán
8. **Order** - Đơn hàng dịch vụ
9. **OrderDetail** - Chi tiết đơn hàng

### Data mẫu (DataSeeder):
- 2 roles: ROLE_ADMIN, ROLE_USER
- 2 users: admin@example.com, user@example.com
- 4 courts: Football Field A, Badminton Court 1, Tennis Court Blue, Futsal Arena

## 🚀 CÁCH SỬ DỤNG

### Scenario 1: User đặt sân
1. Mở http://localhost:8080
2. Click "Login" → nhập user@example.com / password123
3. Tự động vào Dashboard → tab "Available Courts"
4. Chọn sân → click "Book Now"
5. Chọn ngày và giờ → Confirm
6. Quay lại Dashboard → tab "My Bookings" để xem booking vừa tạo

### Scenario 2: Admin quản lý
1. Login với admin@example.com / strongpassword
2. Vào Dashboard → tab "Admin Panel"
3. Xem thống kê: số sân, bookings, users, doanh thu
4. Xem bảng "All Bookings" để quản lý tất cả bookings
5. Filter theo status để tìm bookings cần xử lý

## 🛠️ DOCKER COMMANDS

```bash
# Start
docker compose up -d

# Rebuild
docker compose up --build -d

# Stop
docker compose down

# View logs
docker compose logs app -f

# Restart app only
docker compose restart app
```

## ✨ TÍNH NĂNG NỔI BẬT

- ✅ Multi-architecture (Intel x86_64 + Apple Silicon ARM64)
- ✅ Spring Security với role-based access control
- ✅ REST API đầy đủ với validation
- ✅ Responsive UI với JavaScript dynamic loading
- ✅ Real-time available slots checking
- ✅ Auto price calculation
- ✅ Admin dashboard với statistics
- ✅ Filter & search functionality
- ✅ Docker containerization
- ✅ MySQL database với JPA/Hibernate

## 🎨 UI FEATURES

- 🎨 Modern gradient design
- 📱 Mobile-friendly
- 🔄 Tab navigation
- 🎯 Status badges với màu sắc
- 💰 Price formatting (VNĐ)
- 📅 Date picker validation
- ⏰ Time slots grid display
- 🔍 Filter by type/status

---

**🎉 HỆ THỐNG ĐÃ HOÀN THIỆN VÀ SẴN SÀNG SỬ DỤNG!**
