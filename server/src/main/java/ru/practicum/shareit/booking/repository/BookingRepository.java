package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> getAllByItemOwnerIdOrderByStartDesc(Long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> getAllByBookerIdOrderByStartDesc(Long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b from Booking b " +
            "where b.booker.id = :id and b.end < :currentTime and lower(b.status) = lower('APPROVED') " +
            "order by b.start desc")
    List<Booking> getByBookerIdStatePast(Long id, LocalDateTime currentTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    Booking getTopByItemIdAndStartAfterOrderByStartAsc(Long id, LocalDateTime start);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    Booking getFirstByItemIdAndEndBeforeOrderByEndDesc(Long id, LocalDateTime end);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = :bookerId" +
            " and b.start <= :timeNow" +
            " and b.end > :timeNow" +
            " order by b.start desc")
    Page<Booking> findCurrentBookingsByBookerId(Long bookerId, LocalDateTime timeNow, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = :bookerId" +
            " and b.start <= :timeNow" +
            " and b.end > :timeNow" +
            " order by b.start desc")
    List<Booking> findCurrentBookingsByBookerId(Long bookerId, LocalDateTime timeNow);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = :bookerId" +
            " and b.end <= :endTime" +
            " order by b.start desc")
    Page<Booking> findByBookerIdAndEndInPast(Long bookerId, LocalDateTime endTime, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = :bookerId" +
            " and b.end <= :endTime" +
            " order by b.start desc")
    List<Booking> findByBookerIdAndEndInPast(Long bookerId, LocalDateTime endTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = :bookerId" +
            " and b.start > :startTime" +
            " order by b.start desc")
    Page<Booking> findByBookerIdAndStartInFuture(Long bookerId, LocalDateTime startTime, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = :bookerId" +
            " and b.start > :startTime" +
            " order by b.start desc")
    List<Booking> findByBookerIdAndStartInFuture(Long bookerId, LocalDateTime startTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = :bookerId" +
            " and b.status = :status" +
            " order by b.start desc")
    Page<Booking> findByBookerIdAndStatusContaining(Long bookerId, BookingStatus status, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.booker as u" +
            " where u.id = :bookerId" +
            " and b.status = :status" +
            " order by b.start desc")
    List<Booking> findByBookerIdAndStatusContaining(Long bookerId, BookingStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = ?1" +
            " order by b.start desc")
    Page<Booking> getAllBookingsForOwnersItems(Long userId, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " order by b.start desc")
    List<Booking> getAllBookingsForOwnersItems(Long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " and b.start <= :startTime" +
            " and b.end > :endTime" +
            " order by b.start desc")
    Page<Booking> getCurrentBookingsForOwnersItems(Long userId, LocalDateTime startTime, LocalDateTime endTime,
                                                   Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " and b.start <= :startTime" +
            " and b.end > :endTime" +
            " order by b.start desc")
    List<Booking> getCurrentBookingsForOwnersItems(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " and b.end <= :endTime" +
            " order by b.start desc")
    Page<Booking> getPastBookingsForOwnersItems(Long userId, LocalDateTime endTime, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " and b.end <= :endTime" +
            " order by b.start desc")
    List<Booking> getPastBookingsForOwnersItems(Long userId, LocalDateTime endTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " and b.start > :startTime" +
            " order by b.start desc")
    Page<Booking> getFutureBookingsForOwnersItems(Long userId, LocalDateTime startTime, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " and b.start > :startTime" +
            " order by b.start desc")
    List<Booking> getFutureBookingsForOwnersItems(Long userId, LocalDateTime startTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " and b.status = :status" +
            " order by b.start desc")
    Page<Booking> getBookingsForOwnersWithStatusContaining(Long userId, BookingStatus status, Pageable page);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    @Query("select b" +
            " from Booking as b" +
            " join b.item as i" +
            " join i.owner as o" +
            " where o.id = :userId" +
            " and b.status = :status" +
            " order by b.start desc")
    List<Booking> getBookingsForOwnersWithStatusContaining(Long userId, BookingStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    Booking getFirstByItemIdAndStatusNotAndStartBeforeOrderByEndDesc(long itemId, BookingStatus status,
                                                                     LocalDateTime date);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "booking_entity-graph")
    Booking getFirstByItemIdAndStatusNotAndStartAfterOrderByStart(long itemId, BookingStatus status,
                                                                  LocalDateTime date);
}
