package com.madirex;

import com.madirex.models.Funko;
import com.madirex.models.Model;
import com.madirex.repositories.funko.FunkoRepository;
import com.madirex.repositories.funko.FunkoRepositoryImpl;
import com.madirex.services.database.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de testeo para la clase FunkoRepository
 */
class FunkosRepositoryTestDB {

    private FunkoRepository funkoRepository;

    /**
     * Método que se ejecuta antes de cada test
     */
    @BeforeEach
    void setUp() throws SQLException {
        funkoRepository = FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance());
        funkoRepository.findAll().forEach(e -> {
            try {
                funkoRepository.delete(e.getCod().toString());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * Método que se ejecuta después de cada test
     */
    @AfterEach
    void tearDown() throws SQLException {
        funkoRepository.findAll().forEach(e -> {
            try {
                funkoRepository.delete(e.getCod().toString());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * Test para comprobar que se puede guardar un Funko
     *
     * @throws SQLException Si hay un error en la base de datos
     */
    @Test
    void testSaveFunko() throws SQLException {
        LocalDate date = LocalDate.now();
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.OTROS)
                .price(23.13)
                .releaseDate(date)
                .build();
        Optional<Funko> savedFunko = funkoRepository.save(funko);
        assertTrue(savedFunko.isPresent());
        assertAll(() -> assertNotNull(savedFunko),
                () -> assertNotNull(savedFunko.get().getCod()),
                () -> assertEquals(funko.getName(), savedFunko.get().getName()),
                () -> assertEquals(funko.getModel(), savedFunko.get().getModel()),
                () -> assertEquals(funko.getPrice(), savedFunko.get().getPrice()),
                () -> assertEquals(funko.getReleaseDate(), savedFunko.get().getReleaseDate())
        );
    }

    /**
     * Test para comprobar FindById
     *
     * @throws SQLException Si hay un error en la base de datos
     */
    @Test
    void testFindFunkoById() throws SQLException {
        LocalDate date = LocalDate.now();
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.OTROS)
                .price(23.12)
                .releaseDate(date)
                .build();
        Optional<Funko> savedFunko = funkoRepository.save(funko);
        assertTrue(savedFunko.isPresent());
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.get().getCod().toString());
        assertTrue(foundFunko.isPresent());
        assertAll(() -> assertEquals(funko.getName(), foundFunko.get().getName()),
                () -> assertEquals(funko.getModel(), foundFunko.get().getModel()),
                () -> assertEquals(funko.getPrice(), foundFunko.get().getPrice()),
                () -> assertEquals(funko.getReleaseDate(), foundFunko.get().getReleaseDate()),
                () -> assertNotNull(foundFunko.get().getCod())
        );
    }

    /**
     * Test para comprobar FindAll
     *
     * @throws SQLException Si hay un error en la base de datos
     */
    @Test
    void testFindAllFunkos() throws SQLException {
        funkoRepository.save(Funko.builder()
                .name("test1")
                .model(Model.ANIME)
                .price(12.52)
                .releaseDate(LocalDate.now())
                .build());
        funkoRepository.save(Funko.builder()
                .name("test2")
                .model(Model.ANIME)
                .price(28.52)
                .releaseDate(LocalDate.now())
                .build());
        assertEquals(2, funkoRepository.findAll().size());
    }

    /**
     * Test para comprobar FindByName
     *
     * @throws SQLException Si hay un error en la base de datos
     */
    @Test
    void testFindFunkosByName() throws SQLException {
        LocalDate date = LocalDate.now();
        Funko funko1 = Funko.builder()
                .name("test1")
                .model(Model.ANIME)
                .price(42.23)
                .releaseDate(date)
                .build();
        Funko funko2 = Funko.builder()
                .name("test1")
                .model(Model.OTROS)
                .price(81.23)
                .releaseDate(date)
                .build();
        funkoRepository.save(funko1);
        funkoRepository.save(funko2);
        List<Funko> foundFunkos = funkoRepository.findByName("test1");
        assertAll(() -> assertNotNull(foundFunkos),
                () -> assertEquals(2, foundFunkos.size()),
                () -> assertEquals(foundFunkos.get(0).getName(), funko1.getName()),
                () -> assertEquals(foundFunkos.get(0).getPrice(), funko1.getPrice()),
                () -> assertEquals(foundFunkos.get(0).getReleaseDate(), funko1.getReleaseDate()),
                () -> assertEquals(foundFunkos.get(0).getModel(), funko1.getModel()),
                () -> assertEquals(foundFunkos.get(1).getName(), funko2.getName()),
                () -> assertEquals(foundFunkos.get(1).getPrice(), funko2.getPrice()),
                () -> assertEquals(foundFunkos.get(1).getReleaseDate(), funko2.getReleaseDate()),
                () -> assertEquals(foundFunkos.get(1).getModel(), funko2.getModel())
        );
    }

    /**
     * Test para comprobar Update
     *
     * @throws SQLException Si hay un error en la base de datos
     */
    @Test
    void testUpdateFunko() throws SQLException {
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.ANIME)
                .price(4.42)
                .releaseDate(LocalDate.now())
                .build();
        Optional<Funko> savedFunko = funkoRepository.save(funko);
        assertTrue(savedFunko.isPresent());
        savedFunko.get().setName("Updated");
        savedFunko.get().setPrice(42.43);
        funkoRepository.update(funko.getCod().toString(), savedFunko.get());
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.get().getCod().toString());
        assertTrue(foundFunko.isPresent());
        assertAll(() -> assertEquals(savedFunko.get().getName(), foundFunko.get().getName()),
                () -> assertEquals(savedFunko.get().getPrice(), foundFunko.get().getPrice())
        );
    }

    /**
     * Test para comprobar Delete
     *
     * @throws SQLException Si hay un error en la base de datos
     */
    @Test
    void testDeleteFunko() throws SQLException {
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.ANIME)
                .price(4.42)
                .releaseDate(LocalDate.now())
                .build();
        Optional<Funko> savedFunko = funkoRepository.save(funko);
        assertTrue(savedFunko.isPresent());
        funkoRepository.delete(savedFunko.get().getCod().toString());
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.get().getCod().toString());
        assertAll(() -> assertFalse(foundFunko.isPresent())
        );
    }

    /**
     * Test para comprobar que no se pueda eliminar un Funko que no existe
     *
     * @throws SQLException Si hay un error en la base de datos
     */
    @Test
    void testDeleteFunkoNotExists() throws SQLException {
        boolean deleted = funkoRepository.delete("cac4c061-20ec-4e87-ad3a-b1a7ea12facc");
        assertFalse(deleted);
    }
}