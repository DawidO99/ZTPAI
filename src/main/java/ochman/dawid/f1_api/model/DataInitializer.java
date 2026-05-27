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
                // Alpine F1 Team
                new Driver(null, "Pierre", "Gasly", 10, 0, "Alpine F1 Team", 82),
                new Driver(null, "Franco", "Colapinto", 43, 0, "Alpine F1 Team", 79),

                // Aston Martin
                new Driver(null, "Fernando", "Alonso", 14, 0, "Aston Martin", 90),
                new Driver(null, "Lance", "Stroll", 18, 0, "Aston Martin", 78),
                // Jak Crawford is a reserve, skipping to keeping max 2 per team limit as required

                // Audi
                new Driver(null, "Nico", "Hulkenberg", 27, 0, "Audi", 82),
                new Driver(null, "Gabriel", "Bortoleto", 85, 0, "Audi", 77),

                // Cadillac F1 Team
                new Driver(null, "Sergio", "Perez", 11, 0, "Cadillac F1 Team", 85),
                new Driver(null, "Valtteri", "Bottas", 77, 0, "Cadillac F1 Team", 81),

                // Haas F1 Team
                new Driver(null, "Oliver", "Bearman", 87, 0, "Haas F1 Team", 76),
                new Driver(null, "Esteban", "Ocon", 31, 0, "Haas F1 Team", 82),

                // McLaren
                new Driver(null, "Lando", "Norris", 4, 0, "McLaren", 93),
                new Driver(null, "Oscar", "Piastri", 81, 0, "McLaren", 90),

                // Mercedes
                new Driver(null, "Andrea Kimi", "Antonelli", 12, 0, "Mercedes", 75),
                new Driver(null, "George", "Russell", 63, 0, "Mercedes", 91),

                // Racing Bulls
                new Driver(null, "Liam", "Lawson", 30, 0, "Racing Bulls", 80),
                new Driver(null, "Arvid", "Lindblad", 22, 0, "Racing Bulls", 74),

                // Red Bull Racing
                new Driver(null, "Max", "Verstappen", 1, 0, "Red Bull Racing", 98),
                new Driver(null, "Isack", "Hadjar", 33, 0, "Red Bull Racing", 73),

                // Ferrari
                new Driver(null, "Charles", "Leclerc", 16, 0, "Ferrari", 94),
                new Driver(null, "Lewis", "Hamilton", 44, 0, "Ferrari", 93),

                // Williams
                new Driver(null, "Carlos", "Sainz", 55, 0, "Williams", 91),
                new Driver(null, "Alexander", "Albon", 23, 0, "Williams", 85)
            );
            driverRepository.saveAll(initialDrivers);
        }
    }
}
