# 📖 Hướng Dẫn Sử Dụng Hệ Thống Đặt Sân

## 🚀 Khởi Động Hệ Thống

```bash
docker compose up -d
```

Truy cập: **http://localhost:8080**

---

## 👥 Các Loại Tài Khoản

### 1. 👤 **USER (Khách hàng)**
- **Tài khoản mẫu**: `user@example.com` / `user123`
- **Chức năng**:
  - Xem danh sách sân available
  - Đặt sân
  - Xem lịch sử booking của mình
  - Hủy booking

### 2. 🏗️ **COURT OWNER (Chủ sân)**
- **Tài khoản mẫu**: `owner@example.com` / `owner123`
- **Chức năng**:
  - Thêm sân mới của mình
  - Chỉnh sửa thông tin sân
  - Bật/tắt trạng thái sân (ACTIVE/INACTIVE)
  - Xem danh sách booking cho các sân của mình
  - Không được chỉnh sửa sân của người khác

### 3. ⚙️ **ADMIN (Quản trị viên)**
- **Tài khoản mẫu**: `admin@example.com` / `strongpassword`
- **Chức năng**:
  - Quản lý TẤT CẢ các sân (kể cả của court owners)
  - Xem thống kê tổng quan
  - Xem tất cả bookings
  - Thêm/sửa/xóa bất kỳ sân nào

---

## 🎯 Hướng Dẫn Chi Tiết

### **Đăng Nhập Với Court Owner**

1. Truy cập http://localhost:8080/login
2. Nhập:
   - Email: `owner@example.com`
   - Password: `owner123`
3. Sau khi login, bạn sẽ thấy:
   - Badge **"COURT OWNER"** màu xanh ở góc phải header
   - Tab **"🏗️ My Courts"** trong navigation

### **Thêm Sân Mới (Court Owner)**

1. Click vào tab **"My Courts"**
2. Click button **"➕ Add New Court"**
3. Điền thông tin:
   - **Court Name**: Tên sân (VD: "Sân Bóng Cầu Giấy")
   - **Court Type**: Chọn loại sân (Football/Badminton/Tennis/Futsal)
   - **Location**: Địa chỉ sân
   - **Price per Hour**: Giá thuê/giờ (VNĐ)
   - **Description**: Mô tả (optional)
4. Click **"Add Court"**
5. Sân sẽ tự động:
   - Được gán owner_id = userId của bạn
   - Status = ACTIVE
   - Hiển thị trong danh sách "My Courts"

### **Chỉnh Sửa Sân (Court Owner)**

1. Trong tab **"My Courts"**
2. Click button **"Edit"** ở sân muốn sửa
3. Thay đổi thông tin
4. Click **"Update Court"**
5. ⚠️ **Lưu ý**: Chỉ sửa được sân của MÌNH (có owner_id trùng với userId)

### **Bật/Tắt Sân (Court Owner)**

1. Click button **"Deactivate"** (nếu sân đang ACTIVE)
2. Hoặc click **"Activate"** (nếu sân đang INACTIVE)
3. Sân INACTIVE sẽ không hiển thị cho customers

### **Xem Bookings Của Sân Mình (Court Owner)**

1. Scroll xuống trong tab **"My Courts"**
2. Bảng **"📊 Bookings for My Courts"** hiển thị:
   - Booking ID
   - Court Name
   - Customer Name
   - Date & Time
   - Price
   - Status

---

## 🔒 Security

### **API Endpoints**

#### Court Owner APIs (Chỉ COURT_OWNER access được):
```
GET    /api/court-owner/my-courts        - Lấy danh sách sân của mình
POST   /api/court-owner/courts           - Thêm sân mới
PUT    /api/court-owner/courts/{id}      - Sửa sân (có ownership check)
PATCH  /api/court-owner/courts/{id}/status - Toggle status
GET    /api/court-owner/bookings         - Xem bookings cho sân của mình
```

#### Admin APIs (Chỉ ADMIN access được):
```
GET    /api/admin/courts                 - Xem tất cả sân
POST   /api/admin/courts                 - Thêm sân mới
PUT    /api/admin/courts/{id}            - Sửa bất kỳ sân nào
DELETE /api/admin/courts/{id}            - Xóa sân
GET    /api/admin/stats                  - Thống kê
GET    /api/admin/bookings               - Xem tất cả bookings
```

#### Public APIs:
```
GET    /api/courts                       - Xem sân ACTIVE (không cần login)
GET    /api/courts/{id}                  - Chi tiết sân
POST   /api/bookings                     - Đặt sân (cần login)
```

### **Ownership Verification**

Backend tự động verify:
- Court Owner chỉ sửa được sân có `owner_id = userId`
- Nếu cố sửa sân của người khác → `403 Forbidden`

---

## 📊 Database Schema

### **courts table**
```sql
id                   BIGINT PRIMARY KEY
name                 VARCHAR(100)
type                 VARCHAR(50)
location             VARCHAR(200)
description          TEXT
base_price_per_hour  DECIMAL(10,2)
status               ENUM('ACTIVE', 'INACTIVE')
owner_id             BIGINT NULL             -- FK to users.id
created_at           DATETIME
updated_at           DATETIME
```

### **roles table**
```sql
ROLE_USER         - Khách hàng đặt sân
ROLE_ADMIN        - Quản trị viên hệ thống
ROLE_COURT_OWNER  - Chủ sân
```

---

## 🧪 Test Scenarios

### **Test 1: Court Owner Thêm Sân**
1. Login: `owner@example.com` / `owner123`
2. Click "My Courts" tab
3. Click "Add New Court"
4. Điền: Name="Test Court", Type=FOOTBALL, Location="Hà Nội", Price=300000
5. Submit
6. Verify: Sân xuất hiện trong "My Courts" và có owner_id=3

### **Test 2: Court Owner Không Sửa Được Sân Khác**
1. Login: `owner@example.com`
2. Thử PUT `/api/court-owner/courts/4` (sân có owner_id=NULL)
3. Kết quả: 403 Forbidden "You don't have permission to modify this court"

### **Test 3: Admin Sửa Mọi Sân**
1. Login: `admin@example.com` / `strongpassword`
2. Click "Manage Courts" tab
3. Sửa bất kỳ sân nào (kể cả sân của court owner)
4. Kết quả: Thành công ✅

---

## 🐛 Troubleshooting

### **Không thấy tab "My Courts"**
- ✅ Check: User có role COURT_OWNER không?
- ✅ Run: `docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking -e "SELECT * FROM user_roles WHERE user_id=3"`
- ✅ Phải có: role_id = 3 (ROLE_COURT_OWNER)

### **API trả về 403 Forbidden**
- ✅ Check: Token authentication trong browser
- ✅ Check: User đang login có đúng role không
- ✅ Check: Court owner_id có match với userId không

### **Sân không hiển thị**
- ✅ Check: Sân có status=ACTIVE không?
- ✅ Run: `docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking -e "SELECT * FROM courts"`

---

## 📝 Notes

1. **Court Owner Registration**: Hiện tại court owners được tạo từ DataSeeder. Trong production, cần thêm form đăng ký và admin approve.

2. **Multi-tenancy**: Mỗi court owner chỉ thấy và quản lý sân của mình. Admin thấy tất cả.

3. **Court Status**: 
   - ACTIVE: Hiển thị cho customers, có thể book
   - INACTIVE: Ẩn khỏi danh sách, không book được

4. **Pricing**: Giá được set theo VNĐ/giờ, tự động calculate total price khi booking.

---

## 🎉 Demo Accounts Summary

| Role | Email | Password | Permissions |
|------|-------|----------|-------------|
| USER | user@example.com | user123 | Book courts only |
| COURT_OWNER | owner@example.com | owner123 | Manage own courts |
| ADMIN | admin@example.com | strongpassword | Full system access |

---

Chúc bạn sử dụng hệ thống vui vẻ! 🎯
