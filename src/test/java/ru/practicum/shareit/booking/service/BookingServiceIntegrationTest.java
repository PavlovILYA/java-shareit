package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.in;
import static ru.practicum.shareit.common.TestObjectMaker.makeItem;
import static ru.practicum.shareit.common.TestObjectMaker.makeUser;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data_H2_syntax.sql")
public class BookingServiceIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private BookingService bookingService;

    @Test
    public void checkGetBookingRequestsByUserId_all() {
        List<Booking> bookings = bookingService.getBookingRequestsByUserId(1L, BookingState.ALL, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(5));
        assertThat(bookings.get(0).getId(), is(in(List.of(2L, 4L, 5L, 6L, 7L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(2L, 4L, 5L, 6L, 7L))));
        assertThat(bookings.get(2).getId(), is(in(List.of(2L, 4L, 5L, 6L, 7L))));
        assertThat(bookings.get(3).getId(), is(in(List.of(2L, 4L, 5L, 6L, 7L))));
    }

    @Test
    public void checkGetBookingRequestsByUserId_past() {
        List<Booking> bookings = bookingService.getBookingRequestsByUserId(1L, BookingState.PAST, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4L, 5L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4L, 5L))));
    }

    @Test
    public void checkGetBookingRequestsByUserId_current() {
        List<Booking> bookings = bookingService.getBookingRequestsByUserId(1L, BookingState.CURRENT, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(7L));
    }

    @Test
    public void checkGetBookingRequestsByUserId_future() {
        List<Booking> bookings = bookingService.getBookingRequestsByUserId(1L, BookingState.FUTURE, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(2L, 6L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(2L, 6L))));
    }

    @Test
    public void checkGetBookingRequestsByUserId_waiting() {
        List<Booking> bookings = bookingService.getBookingRequestsByUserId(1L, BookingState.WAITING, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(2L, 6L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(2L, 6L))));
    }

    @Test
    public void checkGetBookingRequestsByUserId_rejected() {
        List<Booking> bookings = bookingService.getBookingRequestsByUserId(1L, BookingState.REJECTED, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(4L));
    }

    @Test
    public void checkGetBookingsByOwnerId_all() {
        List<Booking> bookings = bookingService.getBookingsByOwnerId(3L, BookingState.ALL, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(5));
        assertThat(bookings.get(0).getId(), is(in(List.of(1L, 4L, 5L, 6L, 7L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(1L, 4L, 5L, 6L, 7L))));
        assertThat(bookings.get(2).getId(), is(in(List.of(1L, 4L, 5L, 6L, 7L))));
        assertThat(bookings.get(3).getId(), is(in(List.of(1L, 4L, 5L, 6L, 7L))));
        assertThat(bookings.get(4).getId(), is(in(List.of(1L, 4L, 5L, 6L, 7L))));
    }

    @Test
    public void checkGetBookingsByOwnerId_waiting() {
        List<Booking> bookings = bookingService.getBookingsByOwnerId(3L, BookingState.WAITING, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(6L));
    }

    @Test
    public void checkGetBookingsByOwnerId_current() {
        List<Booking> bookings = bookingService.getBookingsByOwnerId(3L, BookingState.CURRENT, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(1L, 7L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(1L, 7L))));
    }

    @Test
    public void checkGetBookingsByOwnerId_future() {
        List<Booking> bookings = bookingService.getBookingsByOwnerId(3L, BookingState.FUTURE, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(6L));
    }

    @Test
    public void checkGetBookingsByOwnerId_past() {
        List<Booking> bookings = bookingService.getBookingsByOwnerId(3L, BookingState.PAST, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4L, 5L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4L, 5L))));
    }

    @Test
    public void checkGetBookingsByOwnerId_rejected() {
        List<Booking> bookings = bookingService.getBookingsByOwnerId(3L, BookingState.REJECTED, 0, 5);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(4L));
    }

    @Test
    public void checkGetLastBookingByItem() {
        User user = makeUser(3L, "Maria", "maria@ya.ru");
        Item item = makeItem(4L, "Дрель", "На аккумуляторе",
                true, user, null, null);
        Booking booking = bookingService.getLastBookingByItem(item);

        assertThat(booking, notNullValue());
        assertThat(booking.getId(), equalTo(4L));
    }

    @Test
    public void checkGetNextBookingByItem() {
        User user = makeUser(2L, "Anna", "anna@ya.ru");
        Item item = makeItem(3L, "Велик", "Старый велосипед, требуется замена цепи",
                true, user, null, null);
        Booking booking = bookingService.getNextBookingByItem(item);

        assertThat(booking, notNullValue());
        assertThat(booking.getId(), equalTo(2L));
    }
}
