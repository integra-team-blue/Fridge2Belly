package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.dtos.DishDto;
import cloudflight.integra.backend.repository.initial.memory.InMemoryDishRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DishDtoRepositoryTests {

    private final InMemoryDishRepository repo = new InMemoryDishRepository();

    private DishDto sample(String name) {
        return new DishDto(UUID.randomUUID(), name,
                LocalDateTime.now(), 100, 10, 5, 12, List.of(UUID.randomUUID()), List.of(UUID.randomUUID()));
    }

    @Test
    void save_and_findById() {
        DishDto d = sample("Pasta");
        repo.save(d);

        assertTrue(repo.findById(d.getId()).isPresent());
        assertEquals("Pasta", repo.findById(d.getId()).get().getName());
    }

    @Test
    void findAll_returnsAll() {
        repo.save(sample("A"));
        repo.save(sample("B"));
        List<DishDto> all = repo.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void deleteById_removes() {
        DishDto d = sample("ToRemove");
        repo.save(d);
        assertTrue(repo.existsById(d.getId()));
        repo.deleteById(d.getId());
        assertFalse(repo.existsById(d.getId()));
        assertTrue(repo.findAll().isEmpty());
    }
}
