package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.service.DishService;
import cloudflight.integra.backend.model.dtos.DishDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService service;

    public DishController(DishService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DishDto create(@Valid @RequestBody DishDto body) {
        return service.create(body);
    }

    @GetMapping
    public List<DishDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DishDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public DishDto update(@PathVariable UUID id, @Valid @RequestBody DishDto body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
