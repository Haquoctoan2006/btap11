-- =====================================================================
-- TEST DOI TRANG THAI DON HANG TRUC TIEP TRONG MYSQL
-- Chay:  mysql -u root -p12345 ShopOnline
-- Sau moi lenh UPDATE: refresh http://localhost:8080/ShopOnline/orders
-- (doi `orderId = 1` thanh ma don thuc te cua ban)
-- =====================================================================
USE ShopOnline;

-- Xem cac don hien co
SELECT orderId, userId, orderDate, totalAmount, paymentMethod, status FROM `Order` ORDER BY orderId;

UPDATE `Order` SET status = 'NEW'        WHERE orderId = 1;  -- Đơn hàng mới
UPDATE `Order` SET status = 'CONFIRMED'  WHERE orderId = 1;  -- Đã xác nhận
UPDATE `Order` SET status = 'PREPARING'  WHERE orderId = 1;  -- Chuẩn bị hàng
UPDATE `Order` SET status = 'SHIPPING'   WHERE orderId = 1;  -- Vận chuyển
UPDATE `Order` SET status = 'DELIVERING' WHERE orderId = 1;  -- Giao hàng
UPDATE `Order` SET status = 'DELIVERED'  WHERE orderId = 1;  -- Đã giao
UPDATE `Order` SET status = 'CANCELLED'  WHERE orderId = 1;  -- Đơn hàng hủy
UPDATE `Order` SET status = 'RETURNED'   WHERE orderId = 1;  -- Đơn hàng hoàn

-- Kiem tra OrderItem va ton kho sau khi dat hang
SELECT * FROM OrderItem WHERE orderId = 1;
SELECT productId, productName, stock FROM Product;
SELECT * FROM CartItem;
