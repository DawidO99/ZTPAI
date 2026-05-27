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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final ApplicationEventPublisher eventPublisher;

    public List<DriverDto> getAllDrivers(String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return driverRepository.findAll(sort).stream()
                .map(driverMapper::toDto)
                .collect(Collectors.toList());
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
