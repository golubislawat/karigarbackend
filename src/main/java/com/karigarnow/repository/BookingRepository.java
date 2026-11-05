package com.karigarnow.repository;

import com.karigarnow.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query(value = """
        SELECT b FROM Booking b
        JOIN FETCH b.consumer c
        JOIN FETCH b.thekedar t
        JOIN FETCH t.user tu
        JOIN FETCH b.service s
        LEFT JOIN FETCH b.address a
        WHERE b.consumer.id = :consumerId
        ORDER BY b.createdAt DESC
        """,
        countQuery = "SELECT count(b) FROM Booking b WHERE b.consumer.id = :consumerId")
    Page<Booking> findByConsumerIdOrderByCreatedAtDesc(
            @Param("consumerId") UUID consumerId,
            Pageable pageable);

    @Query(value = """
        SELECT b FROM Booking b
        JOIN FETCH b.consumer c
        JOIN FETCH b.thekedar t
        JOIN FETCH t.user tu
        JOIN FETCH b.service s
        LEFT JOIN FETCH b.address a
        WHERE b.consumer.id = :consumerId
        AND b.bookingStatus = :status
        ORDER BY b.createdAt DESC
        """,
        countQuery = "SELECT count(b) FROM Booking b WHERE b.consumer.id = :consumerId AND b.bookingStatus = :status")
    Page<Booking> findByConsumerIdAndBookingStatusOrderByCreatedAtDesc(
            @Param("consumerId") UUID consumerId,
            @Param("status") String status,
            Pageable pageable);

    @Query(value = """
        SELECT b FROM Booking b
        JOIN FETCH b.consumer c
        JOIN FETCH b.thekedar t
        JOIN FETCH t.user tu
        JOIN FETCH b.service s
        LEFT JOIN FETCH b.address a
        WHERE b.thekedar.id = :thekedarId
        ORDER BY b.createdAt DESC
        """,
        countQuery = "SELECT count(b) FROM Booking b WHERE b.thekedar.id = :thekedarId")
    Page<Booking> findByThekedarIdOrderByCreatedAtDesc(
            @Param("thekedarId") UUID thekedarId,
            Pageable pageable);

    @Query(value = """
        SELECT b FROM Booking b
        JOIN FETCH b.consumer c
        JOIN FETCH b.thekedar t
        JOIN FETCH t.user tu
        JOIN FETCH b.service s
        LEFT JOIN FETCH b.address a
        WHERE b.thekedar.id = :thekedarId
        AND b.bookingStatus = :status
        ORDER BY b.createdAt DESC
        """,
        countQuery = "SELECT count(b) FROM Booking b WHERE b.thekedar.id = :thekedarId AND b.bookingStatus = :status")
    Page<Booking> findByThekedarIdAndBookingStatusOrderByCreatedAtDesc(
            @Param("thekedarId") UUID thekedarId,
            @Param("status") String status,
            Pageable pageable);

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.consumer c
        JOIN FETCH b.thekedar t
        JOIN FETCH t.user tu
        JOIN FETCH b.service s
        LEFT JOIN FETCH b.address a
        LEFT JOIN FETCH b.bookingWorkers bw
        LEFT JOIN FETCH bw.worker
        LEFT JOIN FETCH b.reviews
        WHERE b.id = :id
        """)
    Optional<Booking> findByIdWithDetails(@Param("id") UUID id);

    boolean existsByAddressId(UUID addressId);
}
