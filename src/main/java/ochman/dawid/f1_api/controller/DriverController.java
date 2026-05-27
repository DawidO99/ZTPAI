package ochman.dawid.f1_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ochman.dawid.f1_api.dto.DriverDto;
import ochman.dawid.f1_api.dto.TeamStandingDto;
import ochman.dawid.f1_api.service.DriverService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverDto>> getAllDrivers(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(driverService.getAllDrivers(sortBy, dir, search));
    }

    @GetMapping("/standings")
    public ResponseEntity<List<TeamStandingDto>> getTeamStandings() {
        return ResponseEntity.ok(driverService.getTeamStandings());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportDriversCsv() {
        List<DriverDto> drivers = driverService.getAllDrivers("id", "asc", null);
        StringBuilder csv = new StringBuilder("ID,FirstName,LastName,CarNumber,Points,Team\n");
        for (DriverDto d : drivers) {
            csv.append(d.getId()).append(",")
               .append(d.getFirstName()).append(",")
               .append(d.getLastName()).append(",")
               .append(d.getCarNumber()).append(",")
               .append(d.getPoints()).append(",")
               .append(d.getTeam()).append("\n");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "drivers.csv");
        return new ResponseEntity<>(csv.toString().getBytes(), headers, HttpStatus.OK);
    }

    @PostMapping("/simulate-race")
    public ResponseEntity<List<String>> simulateRace() {
        return ResponseEntity.ok(driverService.simulateRace());
    }

    @GetMapping("/season-status")
    public ResponseEntity<java.util.Map<String, Integer>> getSeasonStatus() {
        return ResponseEntity.ok(driverService.getSeasonStatus());
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetSeason() {
        driverService.resetSeason();
        return ResponseEntity.noContent().build();
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
