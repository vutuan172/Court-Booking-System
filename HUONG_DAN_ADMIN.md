# 👨‍💼 HƯỚNG DẪN ADMIN - QUẢN LÝ SÂN

## 🎯 Chức năng Admin đã bổ sung

### Tab mới: **🏗️ Manage Courts**
Tab này cho phép admin **thêm, sửa, bật/tắt sân** trực tiếp từ giao diện web.

---

## 📋 CÁCH SỬ DỤNG

### 1. Đăng nhập Admin
```
URL: http://localhost:8080/login
Email: admin@example.com
Password: strongpassword
```

### 2. Truy cập Dashboard
- Sau khi login, tự động redirect đến `/dashboard`
- Admin sẽ thấy **4 tabs**:
  1. 🏟️ Available Courts (xem sân như user)
  2. 📅 My Bookings (booking của admin)
  3. **🏗️ Manage Courts** ⭐ TAB MỚI
  4. ⚙️ Admin Panel (thống kê & quản lý bookings)

---

## 🏗️ MANAGE COURTS - Chi tiết

### A. Xem danh sách sân
Click tab **"🏗️ Manage Courts"**

**Bảng hiển thị:**
| ID | Name | Type | Location | Price/Hour | Status | Actions |
|----|------|------|----------|------------|--------|---------|
| #1 | Football Field A | FOOTBALL | North District | 150,000 VNĐ | ACTIVE | ✏️ Edit  🚫 Disable |
| #2 | Badminton Court 1 | BADMINTON | Central District | 80,000 VNĐ | ACTIVE | ✏️ Edit  🚫 Disable |
| ... | ... | ... | ... | ... | ... | ... |

### B. Thêm sân mới ➕

**Bước 1:** Click nút **"➕ Add New Court"** (màu xanh lá, góc phải)

**Bước 2:** Popup form hiển thị với các trường:

```
Court Name: _________________
           (ví dụ: Basketball Court 1)

Type:       [Dropdown]
           ├── Football
           ├── Badminton
           ├── Tennis
           └── Futsal

Location:   _________________
           (ví dụ: West District)

Price per Hour (VNĐ): _______
                    (ví dụ: 100000)

Description: _________________
            (tùy chọn, mô tả chi tiết)
```

**Bước 3:** Click **"Add Court"** → Sân mới xuất hiện trong bảng

**Kết quả:**
- ✅ Sân được thêm vào database
- ✅ Status mặc định: ACTIVE
- ✅ Tự động hiển thị trong tab "Available Courts"
- ✅ Users có thể đặt sân ngay lập tức

### C. Sửa sân ✏️

**Bước 1:** Click nút **"✏️ Edit"** ở hàng sân cần sửa

**Bước 2:** Nhập thông tin mới vào các prompt:
- Court name
- Type (FOOTBALL/BADMINTON/TENNIS/FUTSAL)
- Location
- Price per hour
- Description (optional)

**Bước 3:** Confirm → Sân được cập nhật

### D. Bật/Tắt sân 🚫 ✅

**Nút "🚫 Disable" (sân đang ACTIVE):**
- Click → Sân chuyển sang status INACTIVE
- User **KHÔNG thấy** sân này trong "Available Courts"
- Không thể đặt sân này nữa

**Nút "✅ Enable" (sân đang INACTIVE):**
- Click → Sân chuyển sang status ACTIVE
- User **THẤY** sân này trở lại
- Có thể đặt sân bình thường

**Use case:**
- Sân đang bảo trì → Disable
- Sân hoàn thành sửa chữa → Enable
- Sân ngừng hoạt động tạm thời → Disable

---

## 🔧 API ENDPOINTS (cho Admin)

### Quản lý sân
```bash
# Lấy danh sách tất cả sân
GET /api/admin/courts

# Thêm sân mới
POST /api/admin/courts
Body: {
  "name": "Basketball Court 1",
  "type": "BASKETBALL",
  "location": "West District",
  "basePricePerHour": 100000.00,
  "description": "Indoor basketball court with AC",
  "status": "ACTIVE"
}

# Cập nhật sân
PUT /api/admin/courts/{id}
Body: {
  "name": "Football Field A - Updated",
  "type": "FOOTBALL",
  "location": "North District - New Location",
  "basePricePerHour": 180000.00,
  "description": "Updated description",
  "status": "ACTIVE"
}

# Xóa sân (nếu cần implement)
DELETE /api/admin/courts/{id}
```

### Test bằng curl
```bash
# Get all courts (admin)
curl -X GET http://localhost:8080/api/admin/courts \
  -H "Cookie: JSESSIONID=xxx"

# Add new court
curl -X POST http://localhost:8080/api/admin/courts \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=xxx" \
  -d '{
    "name": "Volleyball Court",
    "type": "VOLLEYBALL",
    "location": "East District",
    "basePricePerHour": 90000,
    "description": "Beach volleyball court",
    "status": "ACTIVE"
  }'
```

---

## 📊 WORKFLOW THỰC TẾ

### Scenario 1: Thêm sân mới khi mở rộng
```
1. Admin login
2. Vào tab "Manage Courts"
3. Click "Add New Court"
4. Nhập:
   - Name: "Football Field B"
   - Type: FOOTBALL
   - Location: "South District"
   - Price: 160000
   - Description: "Grass field with lighting"
5. Submit
6. Sân xuất hiện ngay trong danh sách
7. User thấy sân mới và có thể đặt
```

### Scenario 2: Tạm ngừng sân để bảo trì
```
1. Admin vào "Manage Courts"
2. Tìm sân cần bảo trì (ví dụ: Tennis Court Blue)
3. Click nút "🚫 Disable"
4. Sân chuyển sang INACTIVE
5. User không còn thấy sân này trong "Available Courts"
6. Các booking cũ vẫn giữ nguyên (đã confirm)
```

### Scenario 3: Cập nhật giá sân
```
1. Admin vào "Manage Courts"
2. Click "✏️ Edit" trên sân cần tăng giá
3. Nhập price mới: 170000 (tăng từ 150000)
4. Confirm
5. Giá mới áp dụng cho tất cả bookings sau này
```

---

## ✅ CHECKLIST ADMIN

### Khi có sân mới
- [ ] Login admin
- [ ] Vào tab "Manage Courts"
- [ ] Click "Add New Court"
- [ ] Nhập đầy đủ thông tin
- [ ] Verify sân xuất hiện trong bảng
- [ ] Verify sân hiển thị trong tab "Available Courts"
- [ ] Verify user có thể đặt sân mới

### Khi cần sửa sân
- [ ] Vào "Manage Courts"
- [ ] Click "Edit" trên sân cần sửa
- [ ] Nhập thông tin mới
- [ ] Verify thay đổi được lưu
- [ ] Check lại trong "Available Courts"

### Khi cần disable sân
- [ ] Vào "Manage Courts"
- [ ] Click "Disable" trên sân cần tắt
- [ ] Verify status chuyển sang INACTIVE
- [ ] Verify sân biến mất khỏi "Available Courts"
- [ ] Logout và login lại bằng user để test

---

## 🎨 GIAO DIỆN

### Manage Courts Tab
```
┌─────────────────────────────────────────────────────────┐
│ 🏗️ Manage Courts              [➕ Add New Court]        │
├─────────────────────────────────────────────────────────┤
│ ID | Name              | Type      | Location  | Price  │
├────┼───────────────────┼───────────┼───────────┼────────┤
│ #1 | Football Field A  | FOOTBALL  | North     | 150k   │
│    |                   |           |           | [Edit] [Disable] │
├────┼───────────────────┼───────────┼───────────┼────────┤
│ #2 | Badminton Court 1 | BADMINTON | Central   | 80k    │
│    |                   |           |           | [Edit] [Disable] │
└─────────────────────────────────────────────────────────┘
```

### Add Court Modal
```
┌────────────────────────────────┐
│ ➕ Add New Court               │
├────────────────────────────────┤
│ Court Name:                    │
│ [_________________________]    │
│                                │
│ Type:                          │
│ [Football ▼]                   │
│                                │
│ Location:                      │
│ [_________________________]    │
│                                │
│ Price per Hour (VNĐ):          │
│ [_________________________]    │
│                                │
│ Description:                   │
│ [_________________________]    │
│ [_________________________]    │
│                                │
│        [Cancel]  [Add Court]   │
└────────────────────────────────┘
```

---

## 🔐 BẢO MẬT

**Chỉ Admin mới thấy:**
- ✅ Tab "Manage Courts"
- ✅ Nút "Add New Court"
- ✅ Nút "Edit" và "Disable/Enable"

**User thường:**
- ❌ Không thấy tab "Manage Courts"
- ❌ Không thể truy cập `/api/admin/courts`
- ❌ Chỉ xem được sân ACTIVE trong "Available Courts"

**Endpoint protection:**
```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")  // ← Chỉ ADMIN mới gọi được
public class AdminController {
    // ...
}
```

---

## 🎉 KẾT QUẢ

Sau khi bổ sung chức năng này, **ADMIN CÓ THỂ:**

1. ✅ **Tự thêm sân mới** không cần vào database
2. ✅ **Sửa thông tin sân** (tên, giá, địa điểm)
3. ✅ **Bật/tắt sân** theo tình trạng thực tế
4. ✅ **Quản lý toàn bộ** từ giao diện web
5. ✅ **Không cần kiến thức SQL** để thêm data

**Hệ thống hoàn toàn self-service cho admin!** 🚀
