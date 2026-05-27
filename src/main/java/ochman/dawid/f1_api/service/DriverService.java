package ochman.dawid.f1_api.service;

import lombok.RequiredArgsConstructor;
import ochman.dawid.f1_api.dto.DriverDto;
import ochman.dawid.f1_api.exception.ResourceNotFoundException;
import ochman.dawid.f1_api.model.Driver;
import ochman.dawid.f1_api.model.DriverMapper;
import ochman.dawid.f1_api.repository.DriverRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ochman.dawid.f1_api.event.DriverCreatedEvent;
import org.springframework.data.domain.Sort;
import ochman.dawid.f1_api.dto.TeamStandingDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final ApplicationEventPublisher eventPublisher;

    public List<DriverDto> getAllDrivers(String sortBy, String dir, String search) {
        if (search != null && !search.isBlank()) {
            return driverRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(search, search).stream()
                    .map(driverMapper::toDto)
                    .collect(Collectors.toList());
        }
        Sort sort = dir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return driverRepository.findAll(sort).stream()
                .map(driverMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TeamStandingDto> getTeamStandings() {
        return driverRepository.findAll().stream()
                .collect(Collectors.groupingBy(Driver::getTeam, Collectors.summingInt(Driver::getPoints)))
                .entrySet().stream()
                .map(entry -> new TeamStandingDto(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Integer.compare(b.getPoints(), a.getPoints()))
                .collect(Collectors.toList());
    }

    public List<String> simulateRace() {
        List<Driver> allDrivers = driverRepository.findAll();
        if (allDrivers.size() < 10) {
            throw new IllegalStateException("Nie ma wystarczającej liczby kierowców by symulować wyścig.");
        }

        // Tasujemy listę kierowców, by zasymulować losowe wyniki:
        java.util.Collections.shuffle(allDrivers);

        int[] pointsDistribution = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};
        List<String> raceResults = new java.util.ArrayList<>();

        // Przydzielamy punkty dla top 10:
        for (int i = 0; i < 10; i++) {
            Driver driver = allDrivers.get(i);
            driver.setPoints(driver.getPoints() + pointsDistribution[i]);
            driverRepository.save(driver);

            String emoji = (i == 0) ? "🥇" : (i == 1) ? "🥈" : (i == 2) ? "🥉" : "🏎️";
            raceResults.add(emoji + " " + (i + 1) + ". " + driver.getFirstName() + " " + driver.getLastName() + " (+" + pointsDistribution[i] + " pkt)");
        }

        return raceResults;
    }

    public DriverDto getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return driverMapper.toDto(driver);
    }

    public DriverDto createDriver(DriverDto driverDto) {
        if (driverRepository.findByCarNumber(driverDto.getCarNumber()).isPresent()) {
            throw new IllegalArgumentException("Driver with car number " + driverDto.getCarNumber() + " already exists");
        }
        if (driverRepository.countByTeam(driverDto.getTeam()) >= 2) {
            throw new IllegalArgumentException("Team " + driverDto.getTeam() + " already has a maximum of 2 drivers");
        }

        Driver driver = driverMapper.toEntity(driverDto);
        Driver savedDriver = driverRepository.save(driver);

        eventPublisher.publishEvent(new DriverCreatedEvent(this,
                savedDriver.getFirstName() + " " + savedDriver.getLastName(),
                savedDriver.getTeam()));

        return driverMapper.toDto(savedDriver);
    }

    public DriverDto updateDriver(Long id, DriverDto driverDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        Optional<Driver> existingDriverWithNumber = driverRepository.findByCarNumber(driverDto.getCarNumber());
        if (existingDriverWithNumber.isPresent() && !existingDriverWithNumber.get().getId().equals(id)) {
            throw new IllegalArgumentException("Driver with car number " + driverDto.getCarNumber() + " already exists");
        }

        if (driverRepository.countByTeamAndIdNot(driverDto.getTeam(), id) >= 2) {
            throw new IllegalArgumentException("Team " + driverDto.getTeam() + " already has a maximum of 2 drivers");
        }

        driver.setFirstName(driverDto.getFirstName());
        driver.setLastName(driverDto.getLastName());
        driver.setCarNumber(driverDto.getCarNumber());
        driver.setPoints(driverDto.getPoints());
        driver.setTeam(driverDto.getTeam());

        Driver updatedDriver = driverRepository.save(driver);
        return driverMapper.toDto(updatedDriver);
    }

    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        driverRepository.delete(driver);
    }
}
