package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository <Booking, Long>{

    List<Booking> getAllByItemOwnerIdAndStatus(Long userId, BookingStatus waiting);
    List<Booking> getAllByItemOwnerIdOrderByStartDesc(Long userId);
    List<Booking> getAllByBookerIdOrderByStartDesc(Long userId);
    List<Booking> getByBookerIdAndStatus(Long userId, BookingStatus waiting);
    @Query("select b from Booking b " +
            "where b.booker.id = :id and b.end < :currentTime and lower(b.status) = lower('APPROVED') " +
            "order by b.start desc")
    List<Booking> getByBookerIdStatePast(@Param("id") Long id, @Param("currentTime") LocalDateTime localDateTime);

    @Query("select b from Booking b " +
            "where b.booker.id = :id and b.end >= :currentTime and :currentTime >= b.start " +
            "order by b.start desc")
    List<Booking> getByBookerIdStateCurrent(@Param("id") Long id, @Param("currentTime") LocalDateTime localDateTime);

    @Query("select b from Booking b " +
            "where b.booker.id = :id and b.start > :currentTime " +
            "order by b.start desc")
    List<Booking> getFuture(@Param("id") Long id, @Param("currentTime") LocalDateTime localDateTime);

    @Query("select b from Booking b " +
            "join b.item i on b.item = i " +
            "where i.owner.id = :id " +
            "order by b.start desc")
    List<Booking> getOwnerAll(@Param("id") Long id);

    @Query("select b from Booking b " +
            "join b.item i on b.item = i " +
            "where i.owner.id = :id " +
            "and b.end < :currentTime " +
            "order by b.start desc")
    List<Booking> getOwnerPast(@Param("id") Long userId, @Param("currentTime") LocalDateTime localDateTime);

    @Query("select b from Booking b " +
            "join b.item i on b.item = i " +
            "where i.owner.id = :id " +
            "and b.start <= :currentTime and b.end >= :currentTime " +
            "order by b.start desc")
    List<Booking> getOwnerCurrent(@Param("id") Long userId, @Param("currentTime") LocalDateTime localDateTime);

    @Query("select b from Booking b " +
            "join b.item i on b.item = i " +
            "where i.owner.id = :id " +
            "and b.start > :currentTime " +
            "order by b.start desc")
    List<Booking> getOwnerFuture(@Param("id") Long userId, @Param("currentTime") LocalDateTime localDateTime);

    Booking getTopByItemIdAndStartAfterOrderByStartAsc(Long id, LocalDateTime start);
    Booking getFirstByItemIdAndEndBeforeOrderByEndDesc(Long id, LocalDateTime end);
}
