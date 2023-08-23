package com.example.demo.item;

import com.example.demo.dto.ItemDto;
import com.example.demo.exception.IllegalParamException;
import com.example.demo.interfaces.MyInterface;
import com.example.demo.logger.ItemLogger;
import com.example.demo.store.Store;
import com.example.demo.store.StoreRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping(value = "/item")
public class ItemController {

    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;

    public ItemController(ItemRepository itemRepository,
                          StoreRepository storeRepository) {
        this.itemRepository = itemRepository;
        this.storeRepository = storeRepository;
    }
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/create")
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto itemDto) throws NoSuchMethodException {
        MyInterface<String> storeMyInterface = new MyInterface<>();
        Store store = storeRepository.findWithName(storeMyInterface.requireNonNullElseThrow(itemDto.storeName));
        Item item = Item.builder()
                .store(store)
                .price(itemDto.price)
                .name(itemDto.name)
                .description(itemDto.description)
                .build();
        Item save = itemRepository.save(item);
        ItemLogger.createEntity(save);
        ItemDto itemDto1 = ItemDto.builder()
                .id(save.getId())
                .price(save.getPrice())
                .storeName(save.getStore().getName())
                .description(save.getDescription())
                .name(save.getName())
                .build();

        return new ResponseEntity<>(itemDto1, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ItemDto> update(@Valid @RequestBody ItemDto itemDto,@PathVariable Long id) {
        Optional<Item> byId = itemRepository.findById(id);
        Item updatedItem = byId.orElseThrow(IllegalParamException::new);
        updatedItem.setName(itemDto.getName());
        updatedItem.setDescription(itemDto.getDescription());
        updatedItem.setPrice(itemDto.getPrice());
        Item save = itemRepository.save(updatedItem);
        ItemLogger.updateEntity(updatedItem);
        ItemDto itemDto1 = ItemDto.builder()
                .id(save.getId())
                .price(save.getPrice())
                .storeName(save.getStore().getName())
                .description(save.getDescription())
                .name(save.getName())
                .build();
        return new ResponseEntity<>(itemDto1, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('CLIENT')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ItemDto> updateSmall(@RequestBody ItemDto itemDto,@PathVariable Long id) throws NoSuchMethodException {
        MyInterface<String> stringMyInterface = new MyInterface<>();
        Store store = storeRepository.findWithName(stringMyInterface.requireNonNullElseThrow(itemDto.storeName));
        Optional<Item> byId = itemRepository.findById(id);
        Item updatedItem = byId.orElseThrow(IllegalParamException::new);
        updatedItem.setName(Objects.requireNonNullElse(itemDto.getName(),updatedItem.getName()));
        updatedItem.setDescription(Objects.requireNonNullElse(itemDto.getDescription(),updatedItem.getDescription()));
        updatedItem.setPrice(Objects.requireNonNullElse(itemDto.getPrice(),updatedItem.getPrice()));

        MyInterface<Store> storeMyInterface = new MyInterface<>();
            updatedItem.setStore(storeMyInterface.requireNonNullElseThrow(store));

        Item save = itemRepository.save(updatedItem);
        ItemLogger.updateEntity(updatedItem);
        ItemDto itemDto1 = ItemDto.builder()
                .id(save.getId())
                .price(save.getPrice())
                .storeName(save.getStore().getName())
                .description(save.getDescription())
                .name(save.getName())
                .build();
        return new ResponseEntity<>(itemDto1, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Optional<Item> byId = itemRepository.findById(id);
        Item item = byId.orElseThrow(IllegalParamException::new);
        itemRepository.delete(item);
        ItemLogger.deleteEntity(item);
        ItemDto itemDto1 = ItemDto.builder()
                .id(item.getId())
                .price(item.getPrice())
                .storeName(item.getStore().getName())
                .description(item.getDescription())
                .name(item.getName())
                .build();
        return new ResponseEntity<>("Successfully Deleted - "+itemDto1.getName(), HttpStatus.NO_CONTENT);
    }
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/get/{id}")
    public ResponseEntity<ItemDto> get(@PathVariable Long id) {
        Optional<Item> byId = itemRepository.findById(id);
        Item item = byId.orElseThrow(IllegalParamException::new);
        ItemLogger.readEntity(item);
        ItemDto itemDto1 = ItemDto.builder()
                .id(item.getId())
                .price(item.getPrice())
                .storeName(item.getStore().getName())
                .description(item.getDescription())
                .name(item.getName())
                .build();
        return new ResponseEntity<>(itemDto1,HttpStatus.FOUND);
    }
}
