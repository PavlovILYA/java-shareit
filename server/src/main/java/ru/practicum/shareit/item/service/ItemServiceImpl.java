package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item saveItem(Item item) {
        item = itemRepository.save(item);
        log.debug("Saved item: {}", item);
        return item;
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
        storedItem = itemRepository.save(storedItem);
        log.debug("Updated item: {}", storedItem);
        return storedItem;
    }

    @Override
    public Item getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    throw new ItemNotFoundException(itemId);
                });
        log.debug("Returned item: {}", item);
        return item;
    }

    @Override
    public List<Item> getAllByUserId(Long userId, int from, int size) {
        Pageable pageRequest = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.getAllByOwnerIdOrderById(userId, pageRequest).getContent();
        log.debug("Items by userId={}: {}", userId, items);
        return items;
    }

    @Override
    public List<Item> getAllByTemplate(String template, int from, int size) {
        Pageable pageRequest = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.getAllByTemplate(template, pageRequest).getContent();
        log.debug("Items by template={}: {}", template, items);
        return items;
    }

    @Override
    public Comment saveComment(Comment comment) {
        List<Booking> userBookings = bookingRepository.findAllPastByBooker(comment.getAuthor());
        boolean doesAuthorRentThisItem = userBookings.stream().anyMatch(booking ->
                booking.getItem().getId().equals(comment.getItem().getId()));
        if (!doesAuthorRentThisItem) {
            throw new UnavailableItemException(comment.getItem().getId());
        }
        Comment savedComment = commentRepository.save(comment);
        log.debug("Saved comment: {}", savedComment);
        return savedComment;
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
