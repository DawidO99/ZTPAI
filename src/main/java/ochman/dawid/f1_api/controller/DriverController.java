package ochman.dawid.f1_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ochman.dawid.f1_api.dto.DriverDto;
import ochman.dawid.f1_api.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverDto>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PostMapping
    public ResponseEntity<DriverDto> createDriver(@Valid @RequestBody DriverDto driverDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(driverDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDto> updateDriver(@PathVariable Long id, @Valid @RequestBody DriverDto driverDto) {
        return ResponseEntity.ok(driverService.updateDriver(id, driverDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}

