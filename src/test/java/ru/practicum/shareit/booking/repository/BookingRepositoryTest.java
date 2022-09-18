package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.in;
import static ru.practicum.shareit.common.TestObjectMaker.makeItem;
import static ru.practicum.shareit.common.TestObjectMaker.makeUser;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data_H2_syntax.sql")
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private final PageRequest pr = PageRequest.of(0, 5);

    @Test
    public void checkFindAllFutureByBooker() {
        User booker = makeUser(1L, "Petr", "petr@ya.ru");
        List<Booking> bookings = bookingRepository.findAllFutureByBooker(booker, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(2L, 6L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(2L, 6L))));
    }

    @Test
    public void checkFindAllPastByBooker() {
        User booker = makeUser(1L, "Petr", "petr@ya.ru");
        List<Booking> bookings = bookingRepository.findAllPastByBooker(booker, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4L, 5L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4L, 5L))));
    }

    @Test
    public void checkFindAllCurrentByBooker() {
        User booker = makeUser(1L, "Petr", "petr@ya.ru");
        List<Booking> bookings = bookingRepository.findAllPastByBooker(booker, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4L, 5L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4L, 5L))));
    }

    @Test
    public void checkFindAllFutureByOwner() {
        User owner = makeUser(3L, "Maria", "maria@ya.ru");
        List<Booking> bookings = bookingRepository.findAllFutureByOwner(owner, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(6L));
    }

    @Test
    public void checkFindAllPastByOwner() {
        User owner = makeUser(3L, "Maria", "maria@ya.ru");
        List<Booking> bookings = bookingRepository.findAllPastByOwner(owner, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(4L, 5L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(4L, 5L))));
    }

    @Test
    public void checkFindAllCurrentByOwner() {
        User owner = makeUser(3L, "Maria", "maria@ya.ru");
        List<Booking> bookings = bookingRepository.findAllCurrentByOwner(owner, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), is(in(List.of(1L, 7L))));
        assertThat(bookings.get(1).getId(), is(in(List.of(1L, 7L))));
    }

    @Test
    public void checkFindAllPastOrCurrentByItemDesc() {
        Item item = makeItem(6L, "Мультиварка", "В хорошем состоянии", true, null, null, null);
        List<Booking> bookings = bookingRepository.findAllPastOrCurrentByItemDesc(item, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(7L));
    }

    @Test
    public void checkFindAllFutureByItemAsc() {
        Item item = makeItem(6L, "Мультиварка", "В хорошем состоянии", true, null, null, null);
        List<Booking> bookings = bookingRepository.findAllFutureByItemAsc(item, pr).getContent();

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(6L));
    }
}