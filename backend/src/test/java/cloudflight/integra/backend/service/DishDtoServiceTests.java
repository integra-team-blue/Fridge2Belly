package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.repository.initial.memory.InMemoryDishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DishDtoServiceTests {

    private DishService service;

    @BeforeEach
    void setUp() {
        service = new DishService(new InMemoryDishRepository());
    }

    private DishDto req(String name) {
        DishDto d = new DishDto();
        d.setName(name);
        d.setRecipeIds(List.of(UUID.randomUUID()));
        d.setPreparedAt(LocalDateTime.now());
        d.setCalories(120);
        d.setProtein(7);
        d.setFat(3);
        d.setCarbohydrates(15);
        return d;
    }

    @Test
    void create_setsId_andPersists() {
        DishDto created = service.create(req("Salad"));
        assertNotNull(created.getId());
        assertEquals(1,
                     service.getAll()
                             .size());
    }

    @Test
    void getById_returnsOrThrows() {
        DishDto created = service.create(req("Soup"));
        assertEquals("Soup",
                     service.getById(created.getId())
                             .getName());

        UUID missing = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> service.getById(missing));
    }

    @Test
    void update_replacesAndKeepsId() {
        DishDto created = service.create(req("Old"));
        UUID id = created.getId();

        DishDto updated = service.update(id, req("New"));
        assertEquals(id, updated.getId());
        assertEquals("New", updated.getName());
    }

    @Test
    void delete_removes() {
        DishDto created = service.create(req("Temp"));
        service.delete(created.getId());
        assertEquals(0,
                     service.getAll()
                             .size());
    }
}
