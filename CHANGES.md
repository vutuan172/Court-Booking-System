# CHANGES

## Tom tat
- Loai bo hoan toan chuc nang dat dich vu di kem (ServiceItem) khoi he thong.
- Luong dat san giu nguyen, tong tien chi con tinh tu gia lich san.

## Van de phat hien
- Quy trinh booking tao Order/OrderDetail tu ServiceItem va cong vao totalPrice, khong phu hop khi bo dich vu di kem.
- Bao cao top-services phu thuoc order_details nen khong con hop le sau khi loai bo dich vu.
- schema.sql va Bill co lien ket order_id can dong bo khi bo Order.

## Cac thay doi da thuc hien (ghi ro file)
- src/main/java/com/example/booking/entity/Booking.java: bo danh sach orders va cap nhat cong thuc totalPrice.
- src/main/java/com/example/booking/service/impl/BookingServiceImpl.java: bo xu ly orderItems va phu thuoc ServiceItem/Order.
- src/main/java/com/example/booking/dto/BookingRequestDTO.java: bo truong orderItems.
- src/main/java/com/example/booking/entity/Bill.java: bo lien ket order.
- src/main/java/com/example/booking/repository/BillRepository.java: bo findByOrderId.
- src/main/java/com/example/booking/controller/AdminController.java: bo quan ly ServiceItem va bao cao top-services.
- src/main/java/com/example/booking/controller/PublicController.java: bo /api/public/services.
- src/main/java/com/example/booking/service/ReportService.java: bo getTopServices.
- src/main/java/com/example/booking/service/impl/ReportServiceImpl.java: bo thong ke top services.
- src/main/java/com/example/booking/config/SecurityConfig.java: bo /api/orders/**.
- src/main/java/com/example/booking/config/DataSeeder.java: bo tao ServiceItem.
- schema.sql: bo service_items, orders, order_details, va cot order_id trong bills.
- Deleted: src/main/java/com/example/booking/entity/Order.java
- Deleted: src/main/java/com/example/booking/entity/OrderDetail.java
- Deleted: src/main/java/com/example/booking/entity/ServiceItem.java
- Deleted: src/main/java/com/example/booking/entity/enums/OrderStatus.java
- Deleted: src/main/java/com/example/booking/entity/enums/ServiceCategory.java
- Deleted: src/main/java/com/example/booking/dto/OrderItemDTO.java
- Deleted: src/main/java/com/example/booking/dto/OrderRequestDTO.java
- Deleted: src/main/java/com/example/booking/dto/OrderResponseDTO.java
- Deleted: src/main/java/com/example/booking/dto/OrderDetailResponseDTO.java
- Deleted: src/main/java/com/example/booking/dto/ServiceItemRequestDTO.java
- Deleted: src/main/java/com/example/booking/dto/ServiceItemResponseDTO.java
- Deleted: src/main/java/com/example/booking/dto/TopServiceDTO.java
- Deleted: src/main/java/com/example/booking/repository/OrderRepository.java
- Deleted: src/main/java/com/example/booking/repository/OrderDetailRepository.java
- Deleted: src/main/java/com/example/booking/repository/ServiceItemRepository.java
- Deleted: src/main/java/com/example/booking/service/ServiceItemService.java
- Deleted: src/main/java/com/example/booking/service/impl/ServiceItemServiceImpl.java

## Cach chay lai he thong de review
- Docker:
  ```bash
  docker compose up --build
  ```
- Local (JDK 21 + MySQL):
  ```bash
  mvn spring-boot:run
  ```
