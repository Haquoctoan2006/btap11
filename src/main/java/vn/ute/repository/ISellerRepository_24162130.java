package vn.ute.repository;

import java.util.List;

import vn.ute.entity.Seller_24162130;

/** Data Access Layer - truy van bang Seller. */
public interface ISellerRepository_24162130 {

    /** Tat ca cua hang, sap xep theo sellerId tang dan. */
    List<Seller_24162130> findAll();

    Seller_24162130 findById(int sellerId);
}
