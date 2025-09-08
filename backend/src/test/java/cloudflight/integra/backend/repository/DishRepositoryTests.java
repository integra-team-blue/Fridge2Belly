package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Dish;
import cloudflight.integra.backend.repository.inMemory.InMemoryDishRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DishRepositoryTests {

    private final InMemoryDishRepository repo = new InMemoryDishRepository();

    private Dish sample(String name) {
        return new Dish(UUID.randomUUID(), name, UUID.randomUUID(),
                LocalDateTime.now(), 100, 10, 5, 12);
    }

    @Test
    void save_and_findById() {
        Dish d = sample("Pasta");
        repo.save(d);

        assertTrue(repo.findById(d.getId()).isPresent());
        assertEquals("Pasta", repo.findById(d.getId()).get().getName());
    }

    @Test
    void findAll_returnsAll() {
        repo.save(sample("A"));
        repo.save(sample("B"));
        List<Dish> all = repo.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void deleteById_removes() {
        Dish d = sample("ToRemove");
        repo.save(d);
        assertTrue(repo.existsById(d.getId()));
        repo.deleteById(d.getId());
        assertFalse(repo.existsById(d.getId()));
        assertTrue(repo.findAll().isEmpty());
    }
}
