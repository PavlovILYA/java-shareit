package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getAllByOwnerIdOrderById(Long itemId);

    @Query("SELECT i FROM Item AS i" +
            " WHERE i.available = true AND" +
            " LOWER(CONCAT(i.description, i.name)) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Item> getAllByTemplate(String template);
}
