-- =====================================================================
-- ĐỀ THI QUÁ TRÌNH - LẬP TRÌNH WEB - ĐỀ SỐ 05 (HK1 2026-2027)
-- Chuyển từ SQL Server sang MySQL 8
-- Chạy:  mysql -u root -p12345 < database.sql
-- Tài khoản MySQL: root / 12345
-- =====================================================================

DROP DATABASE IF EXISTS ShopOnline;
CREATE DATABASE ShopOnline CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ShopOnline;

-- ---------------------------------------------------------------------
-- 1. UserRoles
-- ---------------------------------------------------------------------
CREATE TABLE UserRoles (
    roleId   INT AUTO_INCREMENT PRIMARY KEY,
    roleName VARCHAR(50) NULL
);

-- ---------------------------------------------------------------------
-- 2. Seller
-- ---------------------------------------------------------------------
CREATE TABLE Seller (
    sellerId   INT AUTO_INCREMENT PRIMARY KEY,
    sellername VARCHAR(50)  NULL,
    images     VARCHAR(500) NULL,
    status     INT          NULL
);

-- ---------------------------------------------------------------------
-- 3. Category
-- ---------------------------------------------------------------------
CREATE TABLE Category (
    categoryId   INT AUTO_INCREMENT PRIMARY KEY,
    categoryName VARCHAR(200) NULL,
    images       VARCHAR(500) NULL,
    status       INT          NULL
);

-- ---------------------------------------------------------------------
-- 4. Users
--    (password để 255 vì SHA-256 dài 64 ký tự, đề gốc là 50)
-- ---------------------------------------------------------------------
CREATE TABLE Users (
    userId   INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NULL,
    email    VARCHAR(100) NULL,
    fullname VARCHAR(50)  NULL,
    password VARCHAR(255) NULL,
    images   VARCHAR(500) NULL,
    phone    VARCHAR(20)  NULL,
    status   INT          NULL,   -- 0 = chưa kích hoạt, 1 = đã kích hoạt
    code     VARCHAR(50)  NULL,   -- mã OTP
    roleId   INT          NULL,
    sellerid INT          NULL,
    CONSTRAINT fk_users_role   FOREIGN KEY (roleId)   REFERENCES UserRoles(roleId),
    CONSTRAINT fk_users_seller FOREIGN KEY (sellerid) REFERENCES Seller(sellerId)
);

-- ---------------------------------------------------------------------
-- 5. Product
-- ---------------------------------------------------------------------
CREATE TABLE Product (
    productId   INT AUTO_INCREMENT PRIMARY KEY,
    productName VARCHAR(200) NULL,
    productCode BIGINT       NULL,
    categoryId  INT          NULL,
    description VARCHAR(500) NULL,
    price       DOUBLE       NULL,
    amount      INT          NULL,
    stock       INT          NULL,
    images      VARCHAR(500) NULL,
    wishlist    INT          NULL,
    status      INT          NULL,
    createDate  DATE         NULL,
    sellerId    INT          NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (categoryId) REFERENCES Category(categoryId),
    CONSTRAINT fk_product_seller   FOREIGN KEY (sellerId)   REFERENCES Seller(sellerId)
);

-- ---------------------------------------------------------------------
-- 6. Cart
--    Đề gốc: cartId nvarchar(50), nhưng đề cũng ghi "tất cả ID đều tăng
--    tự động" => dùng INT AUTO_INCREMENT.
-- ---------------------------------------------------------------------
CREATE TABLE Cart (
    cartId  INT AUTO_INCREMENT PRIMARY KEY,
    userId  INT      NULL,
    buyDate DATETIME NULL,
    status  INT      NULL,
    CONSTRAINT fk_cart_user FOREIGN KEY (userId) REFERENCES Users(userId)
);

-- ---------------------------------------------------------------------
-- 7. CartItem
-- ---------------------------------------------------------------------
CREATE TABLE CartItem (
    cartItemId INT AUTO_INCREMENT PRIMARY KEY,
    quantity   INT    NULL,
    unitPrice  DOUBLE NULL,
    productId  INT    NULL,
    cartId     INT    NULL,
    CONSTRAINT fk_item_product FOREIGN KEY (productId) REFERENCES Product(productId),
    CONSTRAINT fk_item_cart    FOREIGN KEY (cartId)    REFERENCES Cart(cartId)
);

-- ---------------------------------------------------------------------
-- 8. Order  (don hang; "Order" la tu khoa SQL nen luon viet trong dau huyen `Order`)
--    status luu dang chuoi, chi nhan 8 gia tri:
--    NEW, CONFIRMED, PREPARING, SHIPPING, DELIVERING, DELIVERED, CANCELLED, RETURNED
-- ---------------------------------------------------------------------
CREATE TABLE `Order` (
    orderId         INT AUTO_INCREMENT PRIMARY KEY,
    userId          INT          NOT NULL,
    orderDate       DATETIME     NOT NULL,
    totalAmount     DOUBLE       NOT NULL,
    paymentMethod   VARCHAR(20)  NOT NULL DEFAULT 'COD',
    status          VARCHAR(20)  NOT NULL DEFAULT 'NEW',
    shippingAddress VARCHAR(500) NULL,
    phone           VARCHAR(20)  NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (userId) REFERENCES Users(userId),
    CONSTRAINT chk_order_status CHECK (status IN
        ('NEW','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED')),
    INDEX idx_order_user_status (userId, status)
);

-- ---------------------------------------------------------------------
-- 9. OrderItem
-- ---------------------------------------------------------------------
CREATE TABLE OrderItem (
    orderItemId INT AUTO_INCREMENT PRIMARY KEY,
    orderId     INT    NOT NULL,
    productId   INT    NOT NULL,
    quantity    INT    NOT NULL,
    unitPrice   DOUBLE NOT NULL,
    subtotal    DOUBLE NOT NULL,
    CONSTRAINT fk_orderitem_order   FOREIGN KEY (orderId)   REFERENCES `Order`(orderId),
    CONSTRAINT fk_orderitem_product FOREIGN KEY (productId) REFERENCES Product(productId)
);

-- =====================================================================
-- DỮ LIỆU MẪU ("dữ liệu tự thêm vào")
-- Mật khẩu mọi tài khoản mẫu: 123456  (PBKDF2-HMAC-SHA256, cùng định dạng với PasswordUtil_24162130)
-- =====================================================================

INSERT INTO UserRoles (roleName) VALUES ('Admin'), ('User'), ('Seller');

INSERT INTO Seller (sellername, images, status) VALUES
    ('Cua hang Apple',   'seller1.jpg', 1),
    ('Cua hang Samsung', 'seller2.jpg', 1),
    ('Cua hang Xiaomi',  'seller3.jpg', 1);

INSERT INTO Category (categoryName, images, status) VALUES
    ('Dien thoai', 'cate1.jpg', 1),
    ('Laptop',     'cate2.jpg', 1),
    ('Phu kien',   'cate3.jpg', 1);

INSERT INTO Users (username, email, fullname, password, images, phone, status, code, roleId, sellerid) VALUES
    ('admin',   'admin@example.com',   'Quan Tri Vien', 'pbkdf2_sha256$600000$LU4nGY1zlfalObOgYDi+3w==$uffa9BY8xhIITlBHZDtZKtPxT+/PYPuOE9XHAcdCcag=', 'avatar.png', '0900000001', 1, NULL, 1, NULL),
    ('user1',   'user1@example.com',   'Nguyen Van A',  'pbkdf2_sha256$600000$iB4wO5N0KQonaqS7bOSsWw==$lz4n1sT6kCoeWNr+L/yuGd4eypysA1GOAZEBfL0yBig=', 'avatar.png', '0900000002', 1, NULL, 2, NULL),
    ('seller1', 'seller1@example.com', 'Chu Shop Apple','pbkdf2_sha256$600000$64BCc7os762OK5i0PptEmw==$jBjfPfxwgl3NxGxkz4i+bubMrzL+V0PcWwWENWm/6Mk=', 'avatar.png', '0900000003', 1, NULL, 3, 1),
    ('seller2', 'seller2@example.com', 'Chu Shop Samsung','pbkdf2_sha256$600000$ciY2iLJt4U3CD2OjRkVtLA==$NJtFOmCN7r204FZWeEkg+anpP+fxZxQrWrAgtJyoEIk=','avatar.png', '0900000004', 1, NULL, 3, 2);

INSERT INTO Product (productName, productCode, categoryId, description, price, amount, stock, images, wishlist, status, createDate, sellerId) VALUES
    ('iPhone 15',        100001, 1, 'Dien thoai Apple iPhone 15 128GB',   22000000, 50, 50, 'iphone15.jpg',  0, 1, '2026-09-01', 1),
    ('MacBook Air M2',   100002, 2, 'Laptop Apple MacBook Air M2 13 inch', 27000000, 20, 20, 'macbook.jpg',   0, 1, '2026-09-02', 1),
    ('AirPods Pro',      100003, 3, 'Tai nghe khong day chong on',         5500000, 100,100, 'airpods.jpg',   0, 1, '2026-09-03', 1),
    ('Galaxy S24',       200001, 1, 'Dien thoai Samsung Galaxy S24',      19000000, 40, 40, 'galaxys24.jpg', 0, 1, '2026-09-04', 2),
    ('Galaxy Book 3',    200002, 2, 'Laptop Samsung Galaxy Book 3',       18000000, 15, 15, 'galaxybook.jpg',0, 1, '2026-09-05', 2),
    ('Redmi Note 13',    300001, 1, 'Dien thoai Xiaomi Redmi Note 13',     6000000, 80, 80, 'redmi13.jpg',   0, 1, '2026-09-06', 3),
    ('Sac nhanh 65W',    300002, 3, 'Cu sac nhanh Xiaomi 65W',              450000,200,200, 'charger.jpg',   0, 1, '2026-09-07', 3);
