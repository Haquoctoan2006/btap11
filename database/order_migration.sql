-- =====================================================================
-- MIGRATION: them bang `Order` va OrderItem cho database ShopOnline DA CO SAN.
-- KHONG xoa database, KHONG xoa du lieu cu. Co the chay nhieu lan (IF NOT EXISTS).
-- Chay:  mysql -u root -p12345 ShopOnline < database/order_migration.sql
-- (Neu ban tao DB moi tu database.sql thi KHONG can chay file nay.)
-- =====================================================================
USE ShopOnline;

CREATE TABLE IF NOT EXISTS `Order` (
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

CREATE TABLE IF NOT EXISTS OrderItem (
    orderItemId INT AUTO_INCREMENT PRIMARY KEY,
    orderId     INT    NOT NULL,
    productId   INT    NOT NULL,
    quantity    INT    NOT NULL,
    unitPrice   DOUBLE NOT NULL,
    subtotal    DOUBLE NOT NULL,
    CONSTRAINT fk_orderitem_order   FOREIGN KEY (orderId)   REFERENCES `Order`(orderId),
    CONSTRAINT fk_orderitem_product FOREIGN KEY (productId) REFERENCES Product(productId)
);
