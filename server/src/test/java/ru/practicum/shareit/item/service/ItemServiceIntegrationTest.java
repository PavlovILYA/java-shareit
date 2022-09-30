package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data_H2_syntax.sql")
public class ItemServiceIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private ItemService itemService;

    @Test
    public void checkGetAllByUserId_twoItems() {
        List<Item> items = itemService.getAllByUserId(2L, 0, 2);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getId(), is(in(List.of(1L, 3L))));
        assertThat(items.get(0).getName(), is(in(List.of("Отвертка-мультитул", "Велик"))));
        assertThat(items.get(1).getId(), is(in(List.of(1L, 3L))));
        assertThat(items.get(1).getName(), is(in(List.of("Отвертка-мультитул", "Велик"))));
    }

    @Test
    public void checkGetAllByUserId_oneItems() {
        List<Item> items = itemService.getAllByUserId(2L, 0, 1);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(1L));
        assertThat(items.get(0).getName(), equalTo("Отвертка-мультитул"));
    }

    @Test
    public void checkGetAllByTemplate() {
        List<Item> items = itemService.getAllByTemplate("веЛо", 0, 2);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(3L));
        assertThat(items.get(0).getName(), equalTo("Велик"));
        // Item, у которого available=false не показывается
    }

    @Test
    public void checkSetAvailable_andGetAllByTemplate() {
        // Ставим available=true
        Item item = itemService.getItem(2L);
        item.setAvailable(true);
        itemService.updateItem(item);

        // Ставим available=true
        List<Item> items = itemService.getAllByTemplate("веЛосИпЕД", 0, 2);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getId(), is(in(List.of(3L, 2L))));
        assertThat(items.get(0).getName(), is(in(List.of("Велик", "Велосипед"))));
        assertThat(items.get(1).getId(), is(in(List.of(3L, 2L))));
        assertThat(items.get(1).getName(), is(in(List.of("Велик", "Велосипед"))));
    }
}
