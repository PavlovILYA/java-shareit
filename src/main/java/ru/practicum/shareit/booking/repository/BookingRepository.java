package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = :bookerId AND" +
            " b.start > current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllFutureByBookerId(Long bookerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = :bookerId AND" +
            " b.end < current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllPastByBookerId(Long bookerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = :bookerId AND" +
            " b.start < current_timestamp() AND b.end > current_timestamp()" +
            " ORDER BY b.start DESC")
    List<Booking> findAllCurrentByBookerId(Long bookerId);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);
}
