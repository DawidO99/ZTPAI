package ochman.dawid.f1_api.model;

import lombok.RequiredArgsConstructor;
import ochman.dawid.f1_api.repository.DriverRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DriverRepository driverRepository;

    @Override
    public void run(String... args) {
        if (driverRepository.count() == 0) {
            List<Driver> initialDrivers = List.of(
                new Driver(null, "Max", "Verstappen", 1, 400, "Red Bull Racing", 98),
                new Driver(null, "Sergio", "Perez", 11, 250, "Red Bull Racing", 87),
                new Driver(null, "Lewis", "Hamilton", 44, 200, "Mercedes", 94),
                new Driver(null, "George", "Russell", 63, 160, "Mercedes", 90),
                new Driver(null, "Charles", "Leclerc", 16, 210, "Ferrari", 93),
                new Driver(null, "Carlos", "Sainz", 55, 190, "Ferrari", 91),
                new Driver(null, "Lando", "Norris", 4, 180, "McLaren", 92),
                new Driver(null, "Oscar", "Piastri", 81, 110, "McLaren", 89),
                new Driver(null, "Fernando", "Alonso", 14, 200, "Aston Martin", 90),
                new Driver(null, "Lance", "Stroll", 18, 60, "Aston Martin", 78),
                new Driver(null, "Pierre", "Gasly", 10, 50, "Alpine", 82),
                new Driver(null, "Esteban", "Ocon", 31, 48, "Alpine", 82),
                new Driver(null, "Alexander", "Albon", 23, 27, "Williams", 84),
                new Driver(null, "Logan", "Sargeant", 2, 1, "Williams", 70),
                new Driver(null, "Yuki", "Tsunoda", 22, 15, "RB", 80),
                new Driver(null, "Daniel", "Ricciardo", 3, 11, "RB", 81),
                new Driver(null, "Valtteri", "Bottas", 77, 10, "Sauber", 81),
                new Driver(null, "Zhou", "Guanyu", 24, 6, "Sauber", 76),
                new Driver(null, "Kevin", "Magnussen", 20, 5, "Haas", 78),
                new Driver(null, "Nico", "Hulkenberg", 27, 20, "Haas", 82)
            );
            driverRepository.saveAll(initialDrivers);
        }
    }
}
