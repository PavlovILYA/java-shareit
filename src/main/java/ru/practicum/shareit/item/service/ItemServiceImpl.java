package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.InvalidOwnerException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        Item storedItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> {
                    throw new ItemNotFoundException(item.getId());
                });
        if (!storedItem.getOwner().equals(item.getOwner())) {
            throw new InvalidOwnerException("Correct owner is "
                    + storedItem.getOwner()
                    + " but there is "
                    + item.getOwner());
        }
        updateFields(item, storedItem);
        return itemRepository.save(storedItem);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    throw new ItemNotFoundException(itemId);
                });
    }

    @Override
    public List<Item> getAllByUserId(Long userId) {
        return itemRepository.getAllByOwnerIdOrderById(userId);
    }

    @Override
    public List<Item> getAllByTemplate(String template) {
        return itemRepository.getAllByTemplate(template);
    }

    @Override
    public Comment saveComment(Comment comment) {
        List<Booking> userBookings = bookingRepository.findAllPastByBooker(comment.getAuthor());
        boolean doesAuthorRentThisItem = userBookings.stream().anyMatch(booking ->
                booking.getItem().getId().equals(comment.getItem().getId()));
        if (!doesAuthorRentThisItem) {
            throw new UnavailableItemException(comment.getItem().getId());
        }
        return commentRepository.save(comment);
    }

    private void updateFields(Item item, Item storedItem) {
        if (item.getName() != null) {
            storedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            storedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            storedItem.setAvailable(item.getAvailable());
        }
    }
}
