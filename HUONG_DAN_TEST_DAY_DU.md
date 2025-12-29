# 🎯 HƯỚNG DẪN ĐẦY ĐỦ - COURT BOOKING SYSTEM

## ✅ CÁC TÍNH NĂNG ĐÃ TRIỂN KHAI

### 1. Xử Lý Đăng Ký Trùng Email ✅
- ✅ `@Column(unique=true)` trong User entity
- ✅ `userRepository.existsByEmail()` check trước khi register
- ✅ Throw `BookingException("Email already exists")` 
- ✅ Frontend hiển thị lỗi màu đỏ

### 2. Tách Giao Diện Login User & Court Owner ✅
- ✅ `/login/user` → User login page (màu xanh dương)
- ✅ `/login/owner` → Court Owner login page (màu xanh lá)
- ✅ `/register/user` → User registration
- ✅ `/register/owner` → Court Owner registration
- ✅ Custom Success Handler redirect đúng dashboard

### 3. Court Owner Dashboard Riêng ✅
- ✅ ROLE_COURT_OWNER được tạo trong DataSeeder
- ✅ Court entity có field `owner_id`
- ✅ Tab "My Courts" trong dashboard
- ✅ Court Owner chỉ chỉnh sân của mình
- ✅ APIs: GET/POST/PUT/PATCH cho court owner

### 4. Booking Flow Hoạt Động ✅
- ✅ User chọn sân + ngày + giờ
- ✅ BookingService validate slot available
- ✅ Status flow: PENDING → CONFIRMED/REJECTED
- ✅ Court Owner xem và approve bookings

### 5. Frontend Đầy Đủ ✅
- ✅ User dashboard: Danh sách sân, My Bookings
- ✅ Court Owner dashboard: My Courts, Bookings
- ✅ Admin dashboard: Manage Courts, All Bookings
- ✅ Thymeleaf + JavaScript fetch APIs

---

## 📋 DANH SÁCH FILE ĐÃ SỬA/TẠO MỚI

### Backend - Entities
```
src/main/java/com/example/booking/entity/
├── User.java                    [ĐÃ CÓ] - @Column(unique=true) email
├── Court.java                   [ĐÃ SỬA] - Added owner field
├── Booking.java                 [ĐÃ CÓ]
├── Schedule.java                [ĐÃ CÓ]
└── Role.java                    [ĐÃ CÓ]
```

### Backend - DTOs
```
src/main/java/com/example/booking/dto/
├── RegisterRequestDTO.java      [ĐÃ SỬA] - Added roleType field
├── CourtResponseDTO.java        [ĐÃ SỬA] - Added ownerId field
└── ...
```

### Backend - Controllers
```
src/main/java/com/example/booking/controller/
├── AuthController.java          [ĐÃ CÓ]
├── CourtOwnerController.java    [MỚI TẠO] - Court owner APIs
├── ViewController.java          [ĐÃ SỬA] - Login/register routes
└── ...
```

### Backend - Services
```
src/main/java/com/example/booking/service/
├── impl/
│   ├── UserServiceImpl.java    [ĐÃ SỬA] - Support COURT_OWNER role
│   ├── CourtServiceImpl.java   [ĐÃ SỬA] - Owner methods
│   └── ...
```

### Backend - Security
```
src/main/java/com/example/booking/security/
├── CustomAuthenticationSuccessHandler.java  [MỚI TẠO]
├── CustomUserDetailsService.java            [ĐÃ CÓ]
└── ...
```

### Backend - Config
```
src/main/java/com/example/booking/config/
├── SecurityConfig.java          [ĐÃ SỬA] - Custom success handler
└── DataSeeder.java              [ĐÃ SỬA] - Create ROLE_COURT_OWNER
```

### Frontend - Templates
```
src/main/resources/templates/
├── auth/
│   ├── user-login.html          [MỚI TẠO]
│   ├── user-register.html       [MỚI TẠO]
│   ├── owner-login.html         [MỚI TẠO]
│   └── owner-register.html      [MỚI TẠO]
├── dashboard.html               [ĐÃ SỬA] - Added owner tab
└── ...
```

---

## 🚀 HƯỚNG DẪN CHẠY PROJECT

### 1. Khởi Động Hệ Thống
```bash
# Start containers
docker compose up -d

# Check status
docker ps

# View logs
docker logs court-booking-app --tail=50
```

### 2. Truy Cập Hệ Thống
- **Homepage**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health

---

## 🧪 HƯỚNG DẪN TEST

### Test Case 1: Đăng Ký User

#### Bước 1: Đăng ký user mới
```
1. Truy cập: http://localhost:8080/register/user
2. Điền form:
   - Full Name: Nguyen Van A
   - Email: nguyenvana@gmail.com
   - Phone: 0912345678
   - Password: 123456
   - Confirm Password: 123456
3. Click "Register"
```

**Kết quả mong đợi:**
✅ Redirect đến `/login/user?registered=true`
✅ Hiển thị message "Registration successful! Please login."

#### Bước 2: Thử đăng ký lại email trùng
```
1. Truy cập: http://localhost:8080/register/user
2. Điền email: nguyenvana@gmail.com
3. Submit form
```

**Kết quả mong đợi:**
❌ Hiển thị lỗi: "Email already exists"

---

### Test Case 2: Đăng Ký Court Owner

#### Bước 1: Đăng ký court owner mới
```
1. Truy cập: http://localhost:8080/register/owner
2. Điền form:
   - Full Name: Tran Van B Court Management
   - Email: tranvanb@gmail.com
   - Phone: 0987654321
   - Password: 123456
   - Confirm Password: 123456
3. Click "Register as Court Owner"
```

**Kết quả mong đợi:**
✅ Redirect đến `/login/owner?registered=true`
✅ Database có user mới với ROLE_COURT_OWNER

#### Verify trong database:
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking \
  -e "SELECT u.email, r.name FROM users u JOIN user_roles ur ON u.id=ur.user_id JOIN roles r ON ur.role_id=r.id WHERE u.email='tranvanb@gmail.com'"
```

**Expected output:**
```
email                   role
tranvanb@gmail.com      ROLE_COURT_OWNER
```

---

### Test Case 3: Login Tách Biệt

#### Test 3A: User Login
```
1. Truy cập: http://localhost:8080/login/user
2. Login:
   - Email: user@example.com
   - Password: user123
3. Click "Login as Customer"
```

**Kết quả mong đợi:**
✅ Redirect đến `/dashboard`
✅ Header hiển thị tên user (không có badge ADMIN/COURT OWNER)
✅ Thấy tabs: "Available Courts", "My Bookings"
✅ KHÔNG thấy: "My Courts" (court owner), "Manage Courts" (admin)

#### Test 3B: Court Owner Login
```
1. Truy cập: http://localhost:8080/login/owner
2. Login:
   - Email: owner@example.com
   - Password: owner123
3. Click "Login as Court Owner"
```

**Kết quả mong đợi:**
✅ Redirect đến `/dashboard`
✅ Header hiển thị badge "COURT OWNER" màu xanh lá
✅ Thấy tabs: "Available Courts", "My Bookings", **"My Courts"**
✅ Tab "My Courts" có:
   - Button "➕ Add New Court"
   - Table danh sách sân của owner
   - Table bookings cho sân của owner

---

### Test Case 4: Owner Tạo Sân

#### Bước 1: Thêm sân mới
```
1. Login: owner@example.com / owner123
2. Click tab "My Courts"
3. Click button "➕ Add New Court"
4. Điền form:
   - Court Name: Sân Bóng Test Owner
   - Court Type: FOOTBALL
   - Location: Hà Nội, Vietnam
   - Price per Hour: 500000
   - Description: Sân test của owner mới
5. Click "Add Court"
```

**Kết quả mong đợi:**
✅ Alert "Court added successfully!"
✅ Modal đóng
✅ Sân mới xuất hiện trong table "My Courts"
✅ Sân có status ACTIVE

#### Verify API:
```bash
curl -s http://localhost:8080/api/courts | jq '.data[] | select(.name | contains("Test Owner"))'
```

#### Verify database:
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking \
  -e "SELECT id, name, owner_id FROM courts WHERE name LIKE '%Test Owner%'"
```

**Expected:**
```
id    name                    owner_id
16    Sân Bóng Test Owner     3
```

---

### Test Case 5: User Booking Sân

#### Bước 1: User đăng nhập và chọn sân
```
1. Login: user@example.com / user123
2. Tab "Available Courts"
3. Chọn 1 sân (VD: Sân Bóng Đại Học Hà Nội)
4. Click "Book Now"
```

**Kết quả mong đợi:**
✅ Redirect đến `/booking?courtId=1`
✅ Form booking hiển thị:
   - Court name
   - Date picker
   - Time slots available

#### Bước 2: Chọn ngày và giờ
```
1. Chọn date: 2025-12-15
2. Chọn time slot: 14:00 - 15:00
3. Click "Confirm Booking"
```

**Kết quả mong đợi:**
✅ POST `/api/bookings`
✅ Booking created với status = PENDING
✅ Redirect về dashboard
✅ Tab "My Bookings" hiển thị booking mới với status "PENDING"

#### Verify database:
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking \
  -e "SELECT id, user_id, status, total_price FROM bookings ORDER BY id DESC LIMIT 1"
```

---

### Test Case 6: Owner Duyệt Booking

#### Bước 1: Owner xem booking mới
```
1. Login: owner@example.com / owner123
2. Click tab "My Courts"
3. Scroll xuống "📊 Bookings for My Courts"
```

**Kết quả mong đợi:**
✅ Table hiển thị booking vừa tạo
✅ Có thông tin: Customer name, Date, Time, Status=PENDING

#### Bước 2: Accept booking (Manual via API)
```bash
# Get booking ID
BOOKING_ID=<id from database>

# Accept booking
curl -X PATCH http://localhost:8080/api/bookings/$BOOKING_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'
```

**Kết quả mong đợi:**
✅ Status changed: PENDING → CONFIRMED
✅ User refresh dashboard thấy status "CONFIRMED"

#### Verify:
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking \
  -e "SELECT id, status FROM bookings WHERE id=$BOOKING_ID"
```

---

## 📊 KIỂM TRA DATABASE

### 1. Xem tất cả users và roles
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking -e "
SELECT u.id, u.full_name, u.email, GROUP_CONCAT(r.name) as roles 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id 
GROUP BY u.id
"
```

**Expected output:**
```
id  full_name       email                   roles
1   Administrator   admin@example.com       ROLE_ADMIN
2   John Doe        user@example.com        ROLE_USER
3   Court Owner     owner@example.com       ROLE_COURT_OWNER
4   Nguyen Van A    nguyenvana@gmail.com    ROLE_USER
5   Tran Van B      tranvanb@gmail.com      ROLE_COURT_OWNER
```

### 2. Xem courts với owner_id
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking -e "
SELECT id, name, status, owner_id 
FROM courts 
ORDER BY owner_id, id
"
```

### 3. Xem bookings với status
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking -e "
SELECT b.id, u.email as customer, c.name as court, b.status, b.total_price
FROM bookings b
JOIN users u ON b.user_id = u.id
JOIN schedules s ON b.schedule_id = s.id
JOIN courts c ON s.court_id = c.id
ORDER BY b.id DESC
LIMIT 10
"
```

---

## 🔒 SECURITY TESTING

### Test 1: Court Owner Không Sửa Được Sân Khác
```bash
# Login as owner (get session cookie first)
# Then try to edit court owned by another owner

curl -X PUT http://localhost:8080/api/court-owner/courts/10 \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=<your_session>" \
  -d '{
    "name": "Hacked Court",
    "type": "FOOTBALL",
    "location": "Test",
    "basePricePerHour": 100000
  }'
```

**Expected:**
```json
{
  "success": false,
  "message": "You don't have permission to modify this court"
}
```

### Test 2: User Không Access Được Owner API
```bash
# Login as user, try owner API
curl http://localhost:8080/api/court-owner/my-courts \
  -H "Cookie: JSESSIONID=<user_session>"
```

**Expected:** 403 Forbidden

---

## 📝 TÀI KHOẢN MẪU

| Role | Email | Password | Permissions |
|------|-------|----------|-------------|
| **ADMIN** | admin@example.com | strongpassword | Manage all courts, View all bookings |
| **COURT_OWNER** | owner@example.com | owner123 | Manage own courts, View own bookings |
| **USER** | user@example.com | user123 | Book courts, View own bookings |

---

## 🎯 WORKFLOW DIAGRAM

```
┌─────────────┐
│   VISITOR   │
└──────┬──────┘
       │
       ├─────────────────┬─────────────────┐
       ▼                 ▼                 ▼
  [Register User]   [Register Owner]   [Browse Courts]
       │                 │
       ▼                 ▼
  [Login User]      [Login Owner]
       │                 │
       ▼                 ▼
┌─────────────┐   ┌──────────────┐
│USER DASHBOARD│   │OWNER DASHBOARD│
├─────────────┤   ├──────────────┤
│• View Courts│   │• My Courts   │
│• Book Court │   │• Add Court   │
│• My Bookings│   │• Edit Court  │
└──────┬──────┘   │• Bookings    │
       │          └───────┬──────┘
       │                  │
       ▼                  ▼
  [Create Booking]  [Approve/Reject]
  Status: PENDING   Status: CONFIRMED
```

---

## ⚙️ API ENDPOINTS SUMMARY

### Public APIs
```
POST   /auth/register          - Register user/owner
POST   /auth/login             - Login
GET    /api/courts             - List all ACTIVE courts
GET    /api/courts/{id}        - Court details
```

### User APIs (Authenticated)
```
GET    /api/bookings/my-bookings    - My bookings
POST   /api/bookings                - Create booking
PUT    /api/bookings/{id}/cancel    - Cancel booking
```

### Court Owner APIs (ROLE_COURT_OWNER)
```
GET    /api/court-owner/my-courts           - List owner's courts
POST   /api/court-owner/courts              - Add new court
PUT    /api/court-owner/courts/{id}         - Update court (ownership check)
PATCH  /api/court-owner/courts/{id}/status  - Toggle ACTIVE/INACTIVE
GET    /api/court-owner/bookings            - Bookings for owner's courts
```

### Admin APIs (ROLE_ADMIN)
```
GET    /api/admin/courts      - All courts
POST   /api/admin/courts      - Add court
PUT    /api/admin/courts/{id} - Update any court
DELETE /api/admin/courts/{id} - Delete court
GET    /api/admin/bookings    - All bookings
GET    /api/admin/stats       - Statistics
```

---

## 🐛 TROUBLESHOOTING

### Lỗi: "Email already exists"
**Nguyên nhân:** Email đã được đăng ký
**Giải pháp:** Dùng email khác hoặc xóa user trong DB

### Lỗi: Tab "My Courts" không hiển thị
**Nguyên nhân:** User không có role COURT_OWNER
**Kiểm tra:**
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking \
  -e "SELECT * FROM user_roles WHERE user_id=<your_user_id>"
```

### Lỗi: 403 khi owner sửa sân
**Nguyên nhân:** Sân không thuộc owner này
**Kiểm tra:**
```bash
docker exec court-booking-mysql mysql -u booking_user -pbooking_password court_booking \
  -e "SELECT id, name, owner_id FROM courts WHERE id=<court_id>"
```

---

## ✅ CHECKLIST HOÀN THÀNH

- [x] Entity User có unique email
- [x] Validate email trùng trong service
- [x] 2 trang login riêng biệt
- [x] 2 trang register riêng biệt
- [x] ROLE_COURT_OWNER được tạo
- [x] Court có owner_id
- [x] CourtOwnerController với 5 APIs
- [x] Court Owner dashboard UI
- [x] Ownership verification
- [x] Booking flow: PENDING → CONFIRMED/REJECTED
- [x] Frontend đầy đủ cho 3 roles
- [x] SecurityConfig phân quyền đúng
- [x] Custom Success Handler
- [x] Error messages hiển thị

---

🎉 **HỆ THỐNG ĐÃ HOÀN CHỈNH VÀ SẴN SÀNG SỬ DỤNG!**
