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
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getAllByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> getAllByBookerIdOrderByStartDesc(Long userId);

    @Query("select b from Booking b " +
            "where b.booker.id = :id and b.end < :currentTime and lower(b.status) = lower('APPROVED') " +
            "order by b.start desc")
    List<Booking> getByBookerIdStatePast(@Param("id") Long id, @Param("currentTime") LocalDateTime localDateTime);

    Booking getTopByItemIdAndStartAfterOrderByStartAsc(Long id, LocalDateTime start);

    Booking getFirstByItemIdAndEndBeforeOrderByEndDesc(Long id, LocalDateTime end);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = ?1" +
            " and b.start <= ?2" +
            " and b.end > ?2" +
            " order by b.start desc")
    List<Booking> findCurrentBookingsByBookerId(Long bookerId, LocalDateTime timeNow);

    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = ?1" +
            " and b.end <= ?2" +
            " order by b.start desc")
    List<Booking> findByBookerIdAndEndInPast(Long bookerId, LocalDateTime endTime);

    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = ?1" +
            " and b.start > ?2" +
            " order by b.start desc")
    List<Booking> findByBookerIdAndStartInFuture(Long bookerId, LocalDateTime startTime);

    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = ?1" +
            " and b.status = ?2" +
            " order by b.start desc")
    List<Booking> findByBookerIdAndStatusContaining(Long bookerId, BookingStatus status);

    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = ?1" +
            " order by b.start desc")
    List<Booking> getAllBookingsForOwnersItems(Long userId);

    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = ?1" +
            " and b.start <= ?2" +
            " and b.end > ?3" +
            " order by b.start desc")
    List<Booking> getCurrentBookingsForOwnersItems(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = ?1" +
            " and b.end <= ?2" +
            " order by b.start desc")
    List<Booking> getPastBookingsForOwnersItems(Long userId, LocalDateTime endTime);

    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = ?1" +
            " and b.start > ?2" +
            " order by b.start desc")
    List<Booking> getFutureBookingsForOwnersItems(Long userId, LocalDateTime startTime);

    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = ?1" +
            " and b.status = ?2" +
            " order by b.start desc")
    List<Booking> getBookingsForOwnersWithStatusContaining(Long userId, BookingStatus status);

    Booking getFirstByItemIdAndStatusNotAndStartBeforeOrderByEndDesc(long itemId, BookingStatus status,
                                                                     LocalDateTime date);

    Booking getFirstByItemIdAndStatusNotAndStartAfterOrderByStart(long itemId, BookingStatus status,
                                                                  LocalDateTime date);

}
