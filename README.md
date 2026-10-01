# ShopOnline – Đề thi Quá trình số 05 – MSSV 24162130

Servlet + JPA (Hibernate) + JSP + SiteMesh 3, MySQL.
Đã làm thêm phần **User: Giỏ hàng, thanh toán COD, lịch sử đơn hàng + lọc trạng thái** (mục 7). Đã làm: **Câu 1** (cấu trúc 3 tầng, database, SiteMesh 2 vai trò, header/footer), **Câu 2** (đăng ký OTP mail, đăng nhập/đăng xuất Session) và **Câu 3** (sản phẩm gom theo cửa hàng tại `/products`).

## 1. Cài đặt cần có
- JDK 17+ · Maven 3.9+ · MySQL 8 · (VS Code + *Extension Pack for Java* + *Community Server Connectors*)
- **Tomcat 10.1.x** (bắt buộc bản 10.1 vì dùng `jakarta.*`, Tomcat 9 sẽ lỗi)

## 2. Tạo database (MySQL: root / 12345)
```bash
mysql -u root -p12345 < database/database.sql
```
Script tạo database `ShopOnline`, 7 bảng và dữ liệu mẫu. Mật khẩu mọi tài khoản mẫu: `123456`

| Tài khoản | Vai trò | Sau đăng nhập vào |
|---|---|---|
| admin / admin@example.com | Admin | /admin/home (giao diện quản trị) |
| user1 / user1@example.com | User | /home |
| seller1 / seller1@example.com | Seller | /seller/home |

## 3. Sửa 2 chỗ trước khi chạy
1. `src/main/resources/app.properties` → đổi `student.name` thành **họ tên thật** (hiện ở footer).
2. Mail OTP (tuỳ chọn): điền `mail.username` / `mail.password` (Gmail App Password) hoặc đặt biến môi trường
   `MAIL_USERNAME`, `MAIL_PASSWORD`. Nếu để trống, hệ thống **không gửi mail** mà in OTP ra console Tomcat
   (dòng `[DEV] OTP của ... là: 123456`) để bạn test.

## 4. Chạy
**Cách A – Maven (không cần cài Tomcat):**
```bash
mvn clean package
mvn cargo:run
```
**Cách B – VS Code + Tomcat 10.1:** `mvn clean package` → thêm Tomcat 10.1 trong Community Server Connectors →
chuột phải `target/ShopOnline.war` → *Run on Server*.

Mở: http://localhost:8080/ShopOnline/

## 5. Cấu trúc 3 tầng (tên lớp theo quy tắc `Tên_MSSV`)
```
vn.ute
├─ controller   (Presentation/MVC)  Home/Login/Register/SellerHome/AdminHome/ProductBySeller..._24162130
├─ service      (Business)          IUserService/UserService, IProductService/ProductService _24162130
├─ repository   (Data Access)       IUser/IProduct/ISeller + Repository _24162130
├─ entity       (JPA)               User/UserRole/Seller/Category/Product/Cart/CartItem_24162130
├─ filter · listener · utils · dto · config · constants · exception
webapp/WEB-INF: decorators/{main,admin}.jsp · views/** · web.xml · sitemesh3.xml
```

## 6. Câu 3 – `/products` (hoặc `/products/by-seller`)
- Repository: `SellerRepository` lấy tất cả cửa hàng; `ProductRepository` lấy sản phẩm bằng `LEFT JOIN FETCH p.category LEFT JOIN FETCH p.seller` (không bị LazyInitializationException).
- Service `ProductService.getProductsGroupedBySeller()` gom thành `List<SellerProductGroup>`, chỉ 2 truy vấn cho cả trang.
- Ảnh: cột `images` là URL `http(s)://...` thì dùng nguyên, là tên file thì lấy trong `webapp/assets/images/products/`; thiếu ảnh -> `assets/images/no-image.svg`.
- Link tên sản phẩm trỏ tới `/product/detail?id=...` (trang này làm ở Câu 4).

## 7. Chức năng User: Giỏ hàng · Thanh toán COD · Lịch sử đơn hàng
Chỉ tài khoản **User** (vd `user1`) dùng được; chưa đăng nhập sẽ bị chuyển về `/login`, Admin/Seller vào sẽ nhận 403.

| URL | Chức năng |
|---|---|
| `GET /cart` | Xem giỏ hàng (ảnh, đơn giá, số lượng, tồn kho, thành tiền, tổng tiền) |
| `POST /cart/add` · `/cart/update` · `/cart/remove` · `/cart/clear` | Thêm / sửa số lượng / xóa 1 dòng / xóa cả giỏ |
| `GET /checkout` → `POST /checkout/place-order` | Xác nhận đơn, chọn COD, đặt hàng |
| `GET /orders/success?id=` | Đặt hàng thành công |
| `GET /orders` · `GET /orders?status=NEW` | Lịch sử đơn hàng + lọc theo trạng thái |
| `GET /orders/detail?id=` | Chi tiết đơn (chỉ đơn của chính user) |

- **Đặt hàng chạy trong 1 transaction** (`OrderService`): khóa dòng user + sản phẩm (`FOR UPDATE`), đọc lại tồn kho từ DB, tạo `Order` + `OrderItem`, trừ kho, xóa `CartItem`. Lỗi ở bất kỳ bước nào → rollback.
- **Chống IDOR**: `userId` luôn lấy từ Session; mọi truy vấn Cart/Order đều kèm điều kiện thuộc user hiện tại.
- **Database**: bảng `` `Order` `` và `OrderItem` đã nằm trong `database/database.sql`. Nếu DB đã có sẵn, chạy `mysql -u root -p12345 ShopOnline < database/order_migration.sql` (không xóa dữ liệu cũ).
- **Trạng thái đơn** (cột `Order.status`, dạng chuỗi): `NEW`, `CONFIRMED`, `PREPARING`, `SHIPPING`, `DELIVERING`, `DELIVERED`, `CANCELLED`, `RETURNED`. Test đổi trạng thái bằng SQL: `database/test_order_status.sql`.
- **Mail OTP**: không ghi mật khẩu vào `app.properties`; đặt biến môi trường `MAIL_USERNAME`, `MAIL_PASSWORD`.
