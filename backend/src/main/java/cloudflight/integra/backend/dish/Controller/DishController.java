package cloudflight.integra.backend.dish.Controller;

import cloudflight.integra.backend.dish.Service.DishService;
import cloudflight.integra.backend.dish.Model.Dish;
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

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dish create(@Valid @RequestBody Dish body) {
        return service.create(body);
    }

    // READ ALL
    @GetMapping
    public List<Dish> getAll() {
        return service.getAll();
    }

    // READ ONE
    @GetMapping("/{id}")
    public Dish getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // UPDATE (full replace)
    @PutMapping("/{id}")
    public Dish update(@PathVariable UUID id, @Valid @RequestBody Dish body) {
        return service.update(id, body);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
