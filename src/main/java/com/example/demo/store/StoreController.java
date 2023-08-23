package com.example.demo.store;

import com.example.demo.dto.StoreDto;
import com.example.demo.exception.IllegalParamException;
import com.example.demo.exception.NotFoundParamException;
import com.example.demo.interfaces.MyInterface;
import com.example.demo.logger.StoreLogger;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "/store")
public class StoreController {

    private final StoreRepository storeRepository;

    public StoreController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<StoreDto> create(@Valid @RequestBody StoreDto storeDto) {
        Store store = Store.builder()
                .email(storeDto.email)
                .capacity(storeDto.capacity)
                .description(storeDto.description)
                .name(storeDto.name)
                .build();
        Store save = storeRepository.save(store);
        StoreLogger.createEntity(save);
        StoreDto storeDto1 = StoreDto.builder()
                .id(save.getId())
                .description(save.getDescription())
                .email(save.getEmail())
                .capacity(save.getCapacity())
                .name(save.getName())
                .build();
        return new ResponseEntity<>(storeDto1,HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<StoreDto> update(@PathVariable Long id,@Valid @RequestBody StoreDto storeDto) {
        MyInterface<Long> longMyInterface = new MyInterface<>();

        Optional<Store> byId;
        try {
            byId = storeRepository.findById(longMyInterface.requireNonNullElseThrow(id));
        } catch (NoSuchMethodException e) {
            throw new IllegalParamException();
        }
        Store store = byId.orElseThrow(IllegalParamException::new);
        store.setName(storeDto.name);
        store.setDescription(storeDto.description);
        store.setEmail(storeDto.email);
        store.setCapacity(storeDto.capacity);
        Store save = storeRepository.save(store);
        StoreLogger.updateEntity(save);
        StoreDto storeDto1 = StoreDto.builder()
                .id(save.getId())
                .description(save.getDescription())
                .email(save.getEmail())
                .capacity(save.getCapacity())
                .name(save.getName())
                .build();
        return new ResponseEntity<>(storeDto1,HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<StoreDto> updateSmall(@PathVariable Long id, @RequestBody StoreDto storeDto) {
        MyInterface<Long> longMyInterface = new MyInterface<>();
        Optional<Store> byId;
        try {
            byId = storeRepository.findById(longMyInterface.requireNonNullElseThrow(id));
        } catch (NoSuchMethodException e) {
            throw new IllegalParamException();
        }
        Store store = byId.orElseThrow(IllegalParamException::new);
        store.setName(Objects.requireNonNullElse(storeDto.name,store.getName()));
        store.setDescription(Objects.requireNonNullElse(storeDto.description,store.getName()));
        store.setEmail(Objects.requireNonNullElse(storeDto.email,store.getEmail()));
        store.setCapacity(Objects.requireNonNullElse(storeDto.capacity,store.getCapacity()));
        Store save = storeRepository.save(store);
        StoreLogger.updateEntity(save);
        StoreDto storeDto1 = StoreDto.builder()
                .id(save.getId())
                .description(save.getDescription())
                .email(save.getEmail())
                .capacity(save.getCapacity())
                .name(save.getName())
                .build();
        return new ResponseEntity<>(storeDto1,HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        MyInterface<Long> myInterface = new MyInterface<>();
        Optional<Store> byId;
        try {
            byId = storeRepository.findById(myInterface.requireNonNullElseThrow(id));
        } catch (NoSuchMethodException e) {
            throw new IllegalParamException();
        }
        Store store = byId.orElseThrow(NotFoundParamException::new);
        storeRepository.delete(store);
        StoreLogger.deleteEntity(store);
        StoreDto storeDto = StoreDto.builder()
                .id(store.getId())
                .description(store.getDescription())
                .email(store.getEmail())
                .capacity(store.getCapacity())
                .name(store.getName())
                .build();
        return new ResponseEntity<>("Successfully Deleted - "+storeDto.name, HttpStatus.NO_CONTENT);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/{id}")
    public ResponseEntity<StoreDto> get(@PathVariable Long id) {
        MyInterface<Long> myInterface = new MyInterface<>();
        Optional<Store> byId;
        try {
            byId = storeRepository.findById(myInterface.requireNonNullElseThrow(id));
        }catch (NoSuchMethodException e){
            throw new IllegalParamException();
        }
        Store store = byId.orElseThrow(NotFoundParamException::new);
        StoreLogger.deleteEntity(store);
        StoreDto storeDto = StoreDto.builder()
                .id(store.getId())
                .description(store.getDescription())
                .email(store.getEmail())
                .capacity(store.getCapacity())
                .name(store.getName())
                .build();

        return new ResponseEntity<>(storeDto,HttpStatus.FOUND);
    }
}