package ochman.dawid.f1_api.service;

import ochman.dawid.f1_api.dto.DriverDto;
import ochman.dawid.f1_api.exception.ResourceNotFoundException;
import ochman.dawid.f1_api.model.Driver;
import ochman.dawid.f1_api.model.DriverMapper;
import ochman.dawid.f1_api.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DriverService driverService;

    private Driver driver;
    private DriverDto driverDto;

    @BeforeEach
    void setUp() {
        driver = Driver.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Verstappen")
                .carNumber(1)
                .points(400)
                .team("Red Bull Racing")
                .build();

        driverDto = DriverDto.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Verstappen")
                .carNumber(1)
                .points(400)
                .team("Red Bull Racing")
                .build();
    }

    @Test
    void shouldCreateDriver() {
        // given
        when(driverRepository.findByCarNumber(1)).thenReturn(Optional.empty());
        when(driverMapper.toEntity(driverDto)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        // when
        DriverDto result = driverService.createDriver(driverDto);

        // then
        assertNotNull(result);
        assertEquals("Max", result.getFirstName());
        verify(driverRepository, times(1)).save(driver);
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingDriverWithExistingCarNumber() {
        // given
        when(driverRepository.findByCarNumber(1)).thenReturn(Optional.of(driver));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> driverService.createDriver(driverDto));
        verify(driverRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldGetDriverById() {
        // given
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        // when
        DriverDto result = driverService.getDriverById(1L);

        // then
        assertNotNull(result);
        assertEquals("Max", result.getFirstName());
    }

    @Test
    void shouldThrowExceptionWhenDriverNotFound() {
        // given
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> driverService.getDriverById(1L));
    }

    @Test
    void shouldResetSeason() {
        // given
        Driver driver2 = Driver.builder()
                .id(2L).firstName("Lewis").points(50).build();
        List<Driver> drivers = List.of(driver, driver2);

        when(driverRepository.findAll()).thenReturn(drivers);

        // when
        driverService.resetSeason();

        // then
        assertEquals(0, driver.getPoints());
        assertEquals(0, driver2.getPoints());
        verify(driverRepository, times(1)).saveAll(drivers);
    }
}
