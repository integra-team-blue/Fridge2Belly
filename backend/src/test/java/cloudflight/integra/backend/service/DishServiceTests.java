package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.repository.inMemory.InMemoryDishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DishServiceTests {

    private DishService service;

    @BeforeEach
    void setUp() {
        service = new DishService(new InMemoryDishRepository());
    }

    private Dish req(String name) {
        Dish d = new Dish();
        d.setName(name);
        d.setRecipeId(UUID.randomUUID());
        d.setPreparedAt(LocalDateTime.now());
        d.setCalories(120);
        d.setProtein(7);
        d.setFat(3);
        d.setCarbohydrates(15);
        return d;
    }

    @Test
    void create_setsId_andPersists() {
        Dish created = service.create(req("Salad"));
        assertNotNull(created.getId());
        assertEquals(1, service.getAll().size());
    }

    @Test
    void getById_returnsOrThrows() {
        Dish created = service.create(req("Soup"));
        assertEquals("Soup", service.getById(created.getId()).getName());

        UUID missing = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> service.getById(missing));
    }

    @Test
    void update_replacesAndKeepsId() {
        Dish created = service.create(req("Old"));
        UUID id = created.getId();

        Dish updated = service.update(id, req("New"));
        assertEquals(id, updated.getId());
        assertEquals("New", updated.getName());
    }

    @Test
    void delete_removes() {
        Dish created = service.create(req("Temp"));
        service.delete(created.getId());
        assertEquals(0, service.getAll().size());
    }
}
